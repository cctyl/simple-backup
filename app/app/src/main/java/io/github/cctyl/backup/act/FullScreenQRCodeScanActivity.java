package io.github.cctyl.backup.act;


import android.app.Activity;
import android.content.Intent;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.king.camera.scan.AnalyzeResult;
import com.king.camera.scan.CameraScan;
import com.king.camera.scan.analyze.Analyzer;
import com.king.camera.scan.util.PointUtils;
import com.king.view.viewfinderview.ViewfinderView.ViewfinderStyle;
import com.king.zxing.DecodeConfig;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.BarcodeCameraScanActivity;
import com.king.zxing.analyze.QRCodeAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫二维码全屏识别示例
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * <p>
 * <a href="https://github.com/jenly1314">Follow me</a>
 */
public class FullScreenQRCodeScanActivity extends BarcodeCameraScanActivity {

    @Override
    public void initUI() {
        super.initUI();

        // 设置取景框样式
        viewfinderView.setViewfinderStyle(ViewfinderStyle.POPULAR);
    }

    @Override
    public void initCameraScan(CameraScan<Result> cameraScan) {
        super.initCameraScan(cameraScan);
        // 根据需要设置CameraScan相关配置
        cameraScan.setPlayBeep(true);
    }

    @Override
    public Analyzer<Result> createAnalyzer() {
        // 初始化解码配置
        DecodeConfig decodeConfig = new DecodeConfig();
        // 如果只有识别二维码的需求，这样设置效率会更高，不设置默认为DecodeFormatManager.DEFAULT_HINTS
        decodeConfig.setHints(DecodeFormatManager.QR_CODE_HINTS);
        // 设置是否全区域识别，默认false
        decodeConfig.setFullAreaScan(true);

        // BarcodeCameraScanActivity默认使用的MultiFormatAnalyzer，这里可以改为使用QRCodeAnalyzer
        return new QRCodeAnalyzer(decodeConfig);
    }

    /**
     * 布局ID；通过覆写此方法可以自定义布局
     *
     * @return 布局ID
     */
    @Override
    public int getLayoutId() {
        return super.getLayoutId();
    }

    @Override
    public void onScanResultCallback(AnalyzeResult<Result> result) {
        // 停止分析
        getCameraScan().setAnalyzeImage(false);
        // 显示结果点
        displayResultPoint(result);

        // 返回结果
        Intent intent = new Intent();
        intent.putExtra(CameraScan.SCAN_RESULT, result.getResult().getText());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 显示结果点
     */
    private void displayResultPoint(AnalyzeResult<Result> result) {
        int width = result.getImageWidth();
        int height = result.getImageHeight();

        ResultPoint[] resultPoints = result.getResult().getResultPoints();
        int size = resultPoints.length;
        if (size > 0) {
            float x = 0f;
            float y = 0f;
            for (ResultPoint point : resultPoints) {
                x += point.getX();
                y += point.getY();
            }
            float centerX = x / size;
            float centerY = y / size;
            //将实际的结果中心点坐标转换成界面预览的坐标
            android.graphics.Point point = PointUtils.transform(
                    (int) centerX,
                    (int) centerY,
                    width,
                    height,
                    viewfinderView.getWidth(),
                    viewfinderView.getHeight()
            );
            //显示结果点信息
            List<android.graphics.Point> points = new ArrayList<>();
            points.add(point);
            viewfinderView.showResultPoints(points);
        }
    }
}