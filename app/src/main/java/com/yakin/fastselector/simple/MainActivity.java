package com.yakin.fastselector.simple;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yakin.fastselector.ChooseType;
import com.yakin.fastselector.Selector;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.rtp.IRTPGrantHandler;
import com.yakin.rtp.Permission;
import com.yakin.rtp.RTPManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridImageAdapter adapter;

    private ChooseType chooseType = ChooseType.ALL;
    private boolean isMultiSelectMode;
    private int maxSelectNum = 9;

    public final static int REQUEST_CHOOSE = 101;
    public final static int REQUEST_CAMERA = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridImageLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        adapter = new GridImageAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new GridImageAdapter.onItemClickListener() {
            @Override
            public void onSelectClick() {
                RTPManager.get(MainActivity.this).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new IRTPGrantHandler() {
                    @Override
                    public void onPermissionGranted() {
                        Selector.get(MainActivity.this)
                                .openGallery(chooseType)
                                .setMultiple(isMultiSelectMode)
                                .forResult(REQUEST_CHOOSE);
                    }

                    @Override
                    public void onPermissionDenied(Permission[] permissions) {
                        Toast.makeText(MainActivity.this, "无权进行此操作", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onBrowseClick(int position, View view) {

            }
        });

        RadioGroup chooseModeView = findViewById(R.id.choose_mode);
        chooseModeView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.choose_all:
                        chooseType = ChooseType.ALL;
                        break;
                    case R.id.choose_image:
                        chooseType = ChooseType.IMAGE;
                        break;
                    case R.id.choose_video:
                        chooseType = ChooseType.VIDEO;
                        break;
                    case R.id.choose_audio:
                        chooseType = ChooseType.AUDIO;
                        break;
                }
            }
        });

        RadioGroup selectModeView = findViewById(R.id.select_mode);
        selectModeView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.select_single:
                        isMultiSelectMode = false;
                        break;
                    case R.id.select_multiple:
                        isMultiSelectMode = true;
                        break;
                }
            }
        });

        final TextView maxSelectNumView = findViewById(R.id.select_num);
        findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maxSelectNum > 1) {
                    maxSelectNum--;
                    maxSelectNumView.setText(String.valueOf(maxSelectNum));
                }
            }
        });
        findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxSelectNum ++;
                maxSelectNumView.setText(String.valueOf(maxSelectNum));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHOOSE:
                    List<MediaModel> list = Selector.get(MainActivity.this).queryResultFromIntent(data);
                    adapter.setMediaList(list);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
