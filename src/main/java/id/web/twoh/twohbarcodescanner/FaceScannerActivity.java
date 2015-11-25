package id.web.twoh.twohbarcodescanner;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import id.web.twoh.twohbarcodescanner.camera.CameraSourcePreview;
import id.web.twoh.twohbarcodescanner.camera.GraphicOverlay;

/**
 * Created by Hafizh Herdi on 11/24/2015.
 */
public class FaceScannerActivity extends AppCompatActivity {

    private static final int RC_HANDLE_GMS = 9001;
    private static final String TAG = ScannerActivity.class.getSimpleName();
    private FaceDetector faceDetector;
    private Barcode prevBarcode;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);

        faceDetector = new FaceDetector.Builder(this).build();
        FaceTrackerFactory faceFactory = new FaceTrackerFactory(mGraphicOverlay);
        faceDetector.setProcessor(
                new MultiProcessor.Builder<>(faceFactory).build());

        mCameraSource = new CameraSource.Builder(getApplicationContext(), faceDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prevBarcode=null;
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

}
