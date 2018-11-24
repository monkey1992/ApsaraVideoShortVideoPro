package com.aliyun.demo.recorder.view.dialog;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.aliyun.demo.recorder.view.effects.mv.AlivcMVChooseView;
import com.aliyun.demo.recorder.view.effects.mv.MvSelectListener;
import com.aliyun.demo.recorder.view.effects.paster.AlivcPasterChooseView;
import com.aliyun.demo.recorder.view.effects.paster.PasterSelectListener;
import com.aliyun.svideo.sdk.external.struct.form.IMVForm;
import com.aliyun.svideo.sdk.external.struct.form.PreviewPasterForm;

import java.util.ArrayList;
import java.util.List;

public class GIfEffectChooser extends BasePageChooser {
    private MvSelectListener mvSelectListener;
    private PasterSelectListener pasterSelectListener;
    @Override
    public List<Fragment> createPagerFragmentList() {
        List<Fragment> fragments = new ArrayList<>();
        AlivcMVChooseView mvChooseView = new AlivcMVChooseView();
        mvChooseView.setMvSelectListener(new MvSelectListener() {
            @Override
            public void onMvSelected(IMVForm imvForm) {
                Log.e("GIfEffectChooser", "onMvSelected");
                if (mvSelectListener!=null){
                    mvSelectListener.onMvSelected(imvForm);
                }
            }
        });
        AlivcPasterChooseView pasterChooseView = new AlivcPasterChooseView();
        pasterChooseView.setPasterSelectListener(new PasterSelectListener() {
            @Override
            public void onPasterSelected(PreviewPasterForm imvForm) {
                if (pasterSelectListener!=null){
                    pasterSelectListener.onPasterSelected(imvForm);
                }
            }
        });
        fragments.add(pasterChooseView);
        fragments.add(mvChooseView);
        return fragments;
    }

    public void setMvSelectListener(MvSelectListener mvSelectListener) {
        this.mvSelectListener = mvSelectListener;
    }

    public void setPasterSelectListener(PasterSelectListener pasterSelectListener) {
        this.pasterSelectListener = pasterSelectListener;
    }
}
