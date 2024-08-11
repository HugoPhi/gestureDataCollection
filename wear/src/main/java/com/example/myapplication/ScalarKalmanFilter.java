//package Java1.app.src.main.java.com.example.java1;
package com.example.myapplication;

public final class ScalarKalmanFilter {
    private float mX0; // 预测状态
    private float mP0; // 预测协方差
    private float mF; // 实际值：以前实际值
    private float mH; //测量值：实际值
    private float mQ; // 测量噪声
    private float mR; // 环境噪声
    private float mState = 0; // 目前状态
    private float mCovariance = 0.1f; // 目前协方差

    public ScalarKalmanFilter(float f, float h, float q, float r){
        mF = f;
        mH = h;
        mQ = q;
        mR = r;
    }

    public void init(float initialState, float initialCovariance){
        mState = initialState;
        mCovariance = initialCovariance;
    }

    public float correct(float measuredValue){
        // 更新时间-预测
        mX0 = mF * mState;
        mP0 = mF * mCovariance*mF + mQ;

        // 测试更新 - 修正
        float k = mH * mP0/(mH * mP0 * mH + mR);
        mCovariance = (1 - k * mH) * mP0;
        return mState = mX0 + k * (measuredValue - mH * mX0);
    }
}
