package com.epgis.offline.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.epgis.epgisapp.R;

/**
 * Created by Lynn on 2019/3/15.
 */
public class CommonDialog extends Dialog implements View.OnClickListener {

    private TextView tvDialogContent;
    private TextView tvDialogTitle;
    private TextView tvDialogSubmit;
    private TextView tvDialogCancel;

    private Context mContext;
    private String mTitle;
    private String mContent;
    private OnCloseListener mListener;
    private String mPositiveName;
    private String mNegativeName;


    public CommonDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommonDialog(Context context, String content) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.mContent = content;
    }


    public CommonDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.mContent = content;
    }


    public CommonDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.mContent = content;
        this.mListener = listener;
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }


    public CommonDialog setTitle(String title) {
        this.mTitle = title;
        return this;
    }


    public CommonDialog setPositiveButton(String name) {
        this.mPositiveName = name;
        return this;
    }

    public CommonDialog setNegativeButton(String name) {
        this.mNegativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_dialog_commom);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        tvDialogContent = (TextView) findViewById(R.id.tv_dialog_content);
        tvDialogTitle = (TextView) findViewById(R.id.tv_dialog_title);
        tvDialogSubmit = (TextView) findViewById(R.id.tv_dialog_submit);
        tvDialogSubmit.setOnClickListener(this);
        tvDialogCancel = (TextView) findViewById(R.id.tv_dialog_cancel);
        tvDialogCancel.setOnClickListener(this);

        tvDialogContent.setText(mContent);
        if (!TextUtils.isEmpty(mPositiveName)) {
            tvDialogSubmit.setText(mPositiveName);
        }

        if (!TextUtils.isEmpty(mNegativeName)) {
            tvDialogCancel.setText(mNegativeName);
        }

        if (!TextUtils.isEmpty(mTitle)) {
            tvDialogTitle.setText(mTitle);
        } else {
            tvDialogTitle.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_dialog_cancel) {
            if (mListener != null) {
                mListener.onClick(this, false);
            }
            this.dismiss();
        } else if (id == R.id.tv_dialog_submit) {
            if (mListener != null) {
                mListener.onClick(this, true);
            }
        }
    }

    public interface OnCloseListener {
        void onClick(Dialog dialog, boolean confirm);
    }
}
