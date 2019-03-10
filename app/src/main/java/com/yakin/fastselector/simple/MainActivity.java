package com.yakin.fastselector.simple;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yakin.fastselector.ChooseType;
import com.yakin.fastselector.ISelectionHandler;
import com.yakin.fastselector.Selector;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.utils.MimeTypeUtil;
import com.yakin.rtp.IRTPGrantHandler;
import com.yakin.rtp.Permission;
import com.yakin.rtp.RTPManager;

import java.util.List;

public class MainActivity extends AppCompatActivity implements
        RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private TextView maxSelectNumView;

    private ChooseType chooseType = ChooseType.ALL;
    private boolean isMultiSelectMode;
    private int maxSelectNum = 9;

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
                                .forResult(new ISelectionHandler<List<MediaModel>>() {
                                    @Override
                                    public void onSelectionResult(int resultCode, List<MediaModel> list) {
                                        if(resultCode == RESULT_OK) {
                                            adapter.addMediaList(list);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onPermissionDenied(Permission[] permissions) {
                        Toast.makeText(MainActivity.this, "无权进行此操作", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCreateClick() {
                RTPManager.get(MainActivity.this).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new IRTPGrantHandler() {
                    @Override
                    public void onPermissionGranted() {
                        Selector.get(MainActivity.this)
                                .openCreate(chooseType)
                                .setVideoMaxSecond(10)
                                .forResult(new ISelectionHandler<MediaModel>() {
                                    @Override
                                    public void onSelectionResult(int resultCode, MediaModel media) {
                                        if(resultCode == RESULT_OK) {
                                            adapter.addMediaItem(media);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onPermissionDenied(Permission[] permissions) {
                        Toast.makeText(MainActivity.this, "无权进行此操作", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCropClick(final MediaModel media, View view) {
                RTPManager.get(MainActivity.this).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new IRTPGrantHandler() {
                    @Override
                    public void onPermissionGranted() {
                        if(MimeTypeUtil.isImage(media.getMimeType())) {
                            Selector.get(MainActivity.this)
                                    .openCrop(media.getPath())
                                    .forResult(new ISelectionHandler<MediaModel>() {
                                        @Override
                                        public void onSelectionResult(int resultCode, MediaModel media) {
                                            if(resultCode == RESULT_OK) {
                                                adapter.addMediaItem(media);
                                                adapter.notifyDataSetChanged();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onPermissionDenied(Permission[] permissions) {
                        Toast.makeText(MainActivity.this, "无权进行此操作", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onBrowseClick(MediaModel media, View view) {

            }
        });

        RadioGroup chooseModeView = findViewById(R.id.choose_mode);
        chooseModeView.setOnCheckedChangeListener(this);

        RadioGroup selectModeView = findViewById(R.id.select_mode);
        selectModeView.setOnCheckedChangeListener(this);

        maxSelectNumView = findViewById(R.id.select_num);
        findViewById(R.id.minus).setOnClickListener(this);
        findViewById(R.id.plus).setOnClickListener(this);
    }

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
            case R.id.select_single:
                isMultiSelectMode = false;
                break;
            case R.id.select_multiple:
                isMultiSelectMode = true;
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.minus:
                if(maxSelectNum > 1) {
                    maxSelectNum--;
                    maxSelectNumView.setText(String.valueOf(maxSelectNum));
                }
                break;
            case R.id.plus:
                maxSelectNum ++;
                maxSelectNumView.setText(String.valueOf(maxSelectNum));
                break;
        }
    }
}
