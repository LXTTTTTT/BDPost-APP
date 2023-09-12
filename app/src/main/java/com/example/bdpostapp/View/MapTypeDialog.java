package com.example.bdpostapp.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bdpostapp.R;

public class MapTypeDialog extends Dialog {

    private Context context;
    private OnSelectListener listener;
    private LinearLayout normalLL,satelliteLL,topographicLL;
    private ImageView normalSelected,satelliteSelected,topographicSelect;
    private int type;
    public MapTypeDialog(Context context, OnSelectListener listener) {
        super(context);
        this.context=context;
        this.listener=listener;
    }
    public MapTypeDialog(Context context, OnSelectListener listener, int type) {
        super(context);
        this.context=context;
        this.listener=listener;
        this.type=type;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map_type);
        init();
    }
    public void init(){
        normalLL=findViewById(R.id.normalLL);
        satelliteLL=findViewById(R.id.satelliteLL);
        topographicLL=findViewById(R.id.topographicLL);
        normalSelected=findViewById(R.id.normalSelected);
        satelliteSelected=findViewById(R.id.satelliteSelected);
        topographicSelect=findViewById(R.id.topographicSelect);
        if(type==1){
            normalLL.setVisibility(View.GONE);
        }
        normalLL.setOnClickListener(view -> {
            normalSelected.setVisibility(View.VISIBLE);
            satelliteSelected.setVisibility(View.GONE);
            topographicSelect.setVisibility(View.GONE);
            listener.onNormal();
            dismiss();
        });
        satelliteLL.setOnClickListener(view -> {
            normalSelected.setVisibility(View.GONE);
            satelliteSelected.setVisibility(View.VISIBLE);
            topographicSelect.setVisibility(View.GONE);
            listener.onSatellite();
            dismiss();
        });
        topographicLL.setOnClickListener(view -> {
            normalSelected.setVisibility(View.GONE);
            satelliteSelected.setVisibility(View.GONE);
            topographicSelect.setVisibility(View.VISIBLE);
            listener.onTopographic();
            dismiss();
        });

    }
    public interface OnSelectListener{
        void onNormal();
        void onSatellite();
        void onTopographic();
    }
    public static  void show(Dialog dialog){
        if(dialog!=null&&!dialog.isShowing()){
            dialog.show();
        }
    }
    public static  void shutDown(Dialog dialog){
        if(dialog!=null&&dialog.isShowing()){
            dialog.dismiss();
        }
    }
}
