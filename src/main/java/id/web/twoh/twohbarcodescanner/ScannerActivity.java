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

import java.io.IOException;

import id.web.twoh.twohbarcodescanner.camera.CameraSourcePreview;
import id.web.twoh.twohbarcodescanner.camera.GraphicOverlay;

/**
 * Created by Hafizh Herdi on 9/7/2015 www.twoh.co
 */
public class ScannerActivity extends AppCompatActivity {

    private static final int RC_HANDLE_GMS = 9001;
    private static final String TAG = ScannerActivity.class.getSimpleName();
    private BarcodeDetector barcodeDetector;
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

        barcodeDetector = new BarcodeDetector.Builder(this).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, new DetectionListener() {
            @Override
            public void onDetection(Object o) {
                if(o instanceof Barcode) {

                    if(prevBarcode==null)
                    {
                        prevBarcode = (Barcode)o;
                    }

                    if(prevBarcode.rawValue.equals(((Barcode) o).rawValue))
                    {

                    }else if(!prevBarcode.rawValue.equals(((Barcode) o).rawValue)){
                        Toast.makeText(ScannerActivity.this, "Value " + ((Barcode) o).rawValue, Toast.LENGTH_SHORT).show();
                        prevBarcode = (Barcode)o;
                    }
                }
            }
        });
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        mCameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
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
