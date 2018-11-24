package com.aliyun.demo.recorder.faceunity;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.faceunity.wrapper.faceunity;

import java.io.IOException;
import java.io.InputStream;

/**
 * FaceUnify管理类，支持高级美颜(美白，磨皮，红润调整)，美型(脸型，大眼，瘦脸)
 * @author Mulberry
 *         create on 2018/7/11.
 */

public class FaceUnityManager {
    private static final String TAG = "FaceUnityManager";
    static FaceUnityManager faceUnityManager = null;

    /**
     * 美颜道具
     */
    private int mFaceBeautyItem = 0;

    /**
     * 美白
     */
    private float mFaceBeautyColorLevel = 0.2f;
    /**
     * 磨皮
     */
    private float mFaceBeautyBlurLevel = 6.0f;
    /**
     * 精准磨皮,传0和1的值是指是否开启精准磨皮
     */
    private float mFaceBeautyALLBlurLevel = 0.0f;
    /**
     * 红润
     */
    private float mFaceBeautyRedLevel = 0.5f;
    /**
     * 瘦脸
     */
    private float mFaceBeautyCheekThin = 1.0f;
    /**
     * 大眼
     */
    private float mFaceBeautyEnlargeEye = 0.5f;
    /**
     * 美型，脸型选择，有0，1，2，3值可选
     */
    private int mFaceShape = 3;
    /**
     * 美型程度 0-1的值
     */
    private float mFaceShapeLevel = 0.5f;

    public FaceUnityManager() {
    }

    public static FaceUnityManager getInstance(){
        if (faceUnityManager != null){
            return faceUnityManager;
        }

        synchronized (FaceUnityManager.class){
            faceUnityManager = new FaceUnityManager();
            return faceUnityManager;
        }
    }

    /**
     * faceUnity SDK初始化,
     * @param context
     */
    public boolean setUp(Context context){
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("v3.bundle");
            byte[] v3data= new byte[inputStream.available()];
            int len = inputStream.read(v3data);
            inputStream.close();

            /**
             * SDK初始化
             */
            faceunity.fuSetup(v3data, null, authpack.A());
            Log.e(TAG, "fuSetup v3 len " + len);


            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建美颜相关
     * @param context
     * @return
     */
    public boolean createBeautyItem(Context context){
        InputStream inputStream = null;
        /**
         * 美颜初始化
         */
        try {
            inputStream = context.getAssets().open("face_beautification.bundle");
            byte[] itemData = new byte[inputStream.available()];
            int len = inputStream.read(itemData);
            Log.e(TAG, "beautification len " + len);
            inputStream.close();
            mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     *
     * @param cameraNV21Byte cameraNV21原始数据
     * @param fuImgNV21Bytes 为用来人脸识别的图像内存数据
     * @param cameraTextureId 为用来绘制的texture id
     * @param cameraWidth 摄像头采集数据的宽
     * @param cameraHeight 摄像头采集数据的高
     * @param frameId 为当前帧数序号，重新初始化后需要从0开始
     * @param currentCameraType
     */
    public int draw(byte[] cameraNV21Byte, byte[] fuImgNV21Bytes, int cameraTextureId, int cameraWidth, int cameraHeight, int frameId, int currentCameraType){
        final int isTracking = faceunity.fuIsTracking();
        faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "skin_detect", mFaceBeautyALLBlurLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
        faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
        faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
        faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);

        boolean isOESTexture = true; //Tip: camera texture类型是默认的是OES的，和texture 2D不同
        int flags = isOESTexture ? faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE : 0;
        boolean isNeedReadBack = false; //是否需要写回，如果是，则入参的byte[]会被修改为带有fu特效的；支持写回自定义大小的内存数组中，即readback custom img
        flags = isNeedReadBack ? flags | faceunity.FU_ADM_FLAG_ENABLE_READBACK : flags;
        if (isNeedReadBack) {
            if (fuImgNV21Bytes == null) {
                fuImgNV21Bytes = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, fuImgNV21Bytes, 0, cameraNV21Byte.length);
        } else {
            fuImgNV21Bytes = cameraNV21Byte;
        }
        flags |= currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X;

            /*
             * 这里拿到fu处理过后的texture，可以对这个texture做后续操作，如硬编、预览。
             */
        return faceunity.fuDualInputToTexture(fuImgNV21Bytes, cameraTextureId, flags,
            cameraWidth, cameraHeight, frameId,  new int[]{mFaceBeautyItem});
    }

    /**
     * 美白
     * @param mFaceBeautyColorLevel
     */
    public void setFaceBeautyColorLevel(float mFaceBeautyColorLevel) {
        this.mFaceBeautyColorLevel = mFaceBeautyColorLevel;
    }

    /**
     * 磨皮
     * @param mFaceBeautyBlurLevel
     */
    public void setFaceBeautyBlurLevel(float mFaceBeautyBlurLevel) {
        this.mFaceBeautyBlurLevel = mFaceBeautyBlurLevel;
    }

    /**
     * 精准磨皮
     * @param mFaceBeautyALLBlurLevel
     */
    public void setFaceBeautyALLBlurLevel(float mFaceBeautyALLBlurLevel) {
        this.mFaceBeautyALLBlurLevel = mFaceBeautyALLBlurLevel;
    }


    /**
     * 瘦脸
     * @return
     */
    public void setFaceBeautyCheekThin(float mFaceBeautyCheekThin) {
        this.mFaceBeautyCheekThin = mFaceBeautyCheekThin;
    }

    /**
     * 大眼
     * @param mFaceBeautyEnlargeEye
     */
    public void setFaceBeautyEnlargeEye(float mFaceBeautyEnlargeEye) {
        this.mFaceBeautyEnlargeEye = mFaceBeautyEnlargeEye;
    }

    /**
     * 红润
     * @return
     */
    public void setFaceBeautyRedLevel(float mFaceBeautyRedLevel) {
        this.mFaceBeautyRedLevel = mFaceBeautyRedLevel;
    }

    /**
     * 美型，脸型选择
     * @param mFaceShape
     */
    public void setFaceShape(int mFaceShape) {
        this.mFaceShape = mFaceShape;
    }

    /**
     * 美型程度
     * @param mFaceShapeLevel 0-1的值
     */
    public void setFaceShapeLevel(float mFaceShapeLevel) {
        this.mFaceShapeLevel = mFaceShapeLevel;
    }


    public void release(){
        faceunity.fuDestroyAllItems();
        faceunity.fuDone();
        faceunity.fuOnDeviceLost();
    }
}
