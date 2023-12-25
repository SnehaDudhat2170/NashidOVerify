package com.kyc.nashid.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Size;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.ViewModelProvider;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.kyc.nashid.BuildConfig;
import com.kyc.nashid.R;
import com.kyc.nashid.databinding.ActivityQrImplemetationBinding;
import com.kyc.nashid.login.networking.APIClient;
import com.kyc.nashid.login.networking.APIInterface;
import com.kyc.nashid.login.networking.AllUrls;
import com.kyc.nashid.login.utils.LoaderUtil;
import com.kyc.nashid.login.utils.SecurePreferences;
import com.kyc.nashidmrz.Logger;
import com.kyc.nashidmrz.Utility;
import com.kyc.nashidmrz.mrtd2.CameraXViewModel;
import com.kyc.nashidmrz.mrtd2.PreferenceUtils;
import com.kyc.nashidmrz.mrtd2.activity.InstructionScreenActivity;
import com.kyc.nashidmrz.mrtd2.activity.StartScanningActivity;
import com.kyc.nashidmrz.mrtd2.activity.TextSizeConverter;
import com.kyc.nashidmrz.mrtd2.rooted.RootedCheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRImplementationActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "CameraXLivePreview";
    private Executor executor = Executors.newSingleThreadExecutor();
    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;
    private ImageCapture imageCapture;
    private SurfaceHolder holder;
    private Canvas canvas;
    private Paint paint;
    private int boxWidth, boxHeight;
    private boolean flag = true, isFrontView = false, isFrontViewScanned = false;
    private int left, right, top, bottom, diameter;
    private ActivityQrImplemetationBinding binding;
    private Bitmap croppedBitmap = null;
    private double screenDivideValue = 1.8;
    private final Logger logger = Logger.withTag(this.getClass().getSimpleName());
    private TextSizeConverter textSizeConverter;
    private BarcodeScannerOptions options;
    private BarcodeScanner scanner;
    private String selectedDocType;
    private boolean instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RootedCheck.getInstance().setFlag(QRImplementationActivity.this);
        if (RootedCheck.getInstance().isRootedDevice(getApplicationContext())) {
            RootedCheck.getInstance().showRootedDeviceDialog(this, getString(R.string.root_dialog), getString(R.string.root_desc), getString(R.string.root_btn));
        } else {
            Utility.getInstance().setDeviceHeight(getScreenHeight());
            Utility.getInstance().setGetDeviceWidth(getScreenWidth());
            cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
            binding = ActivityQrImplemetationBinding.inflate(getLayoutInflater());

            View view = binding.getRoot();
            setContentView(view);

            initView();
            initClick();
        }
    }

    private void initClick() {
        binding.layoutHeader.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.cardBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code=binding.edtEnterCode.getText().toString();
                if (code.length()!=0){
                    callValidateScanCode(code);
                }else{
                    Toast.makeText(getApplicationContext(),"Enter the code",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void setLayoutAndTextSize() {
        textSizeConverter = new TextSizeConverter(getApplicationContext());
//        textSizeConverter.changeStatusBarColor(QRImplementationActivity.this);
        LinearLayout.LayoutParams layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(4), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);
        binding.layoutHeader.txtHelp.setText(getString(R.string.txt_scan_header));
        binding.layoutHeader.txtHelp.setTextColor(Color.WHITE);
        binding.layoutHeader.txtHelp.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        binding.layoutHeader.txtHelp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(17));
        int padding = textSizeConverter.getPaddingORMarginValue(6);
        binding.layoutHeader.txtHelp.setPadding(0, padding, 0, 0);
        ViewGroup.LayoutParams layoutParams2 = binding.layoutHeader.imgBack.getLayoutParams();
        layoutParams2.height = textSizeConverter.getHeight(Integer.parseInt(getString(R.string.header_back_icon_size)));
        binding.layoutHeader.imgBack.setLayoutParams(layoutParams2);

        SVG svg = null;
        try {
            svg = SVG.getFromResource(getResources(), com.kyc.nashidmrz.R.raw.back_white);
            binding.layoutHeader.imgBack.setSVG(svg);
        } catch (SVGParseException e) {
        }

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(42), 0, 0);
        binding.layoutHeader.lytHeaderMain.setLayoutParams(layoutParams);

         padding = textSizeConverter.getPaddingORMarginValue(26);
        int padding1 = textSizeConverter.getPaddingORMarginValue(36);
        binding.txtLine1.setPadding(0, padding, 0, padding1);
        binding.txtLine1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(28));
        binding.txtLine2.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        padding = textSizeConverter.getPaddingORMarginValue(16);
        binding.qrParent.setPadding(padding, padding, padding, padding);
        padding = textSizeConverter.getPaddingORMarginValue(12);
        binding.btnNext.setPadding(0, padding, 0, padding);
        binding.btnNext.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));
        binding.cardBtnRegister.setRadius(textSizeConverter.calculateRadius(8));

        int edtPadding = textSizeConverter.getPaddingORMarginValue(10);
        binding.edtEnterCode.setPadding(textSizeConverter.getPaddingORMarginValue(20), edtPadding, textSizeConverter.getPaddingORMarginValue(20), edtPadding);
        binding.edtEnterCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeConverter.getTextSize(16));

        layoutParams = textSizeConverter.getLinearLayoutParam();
        layoutParams.setMargins(0, textSizeConverter.getPaddingORMarginValue(40), 0, textSizeConverter.getPaddingORMarginValue(40));
        binding.edtEnterCode.setLayoutParams(layoutParams);

        binding.txtEnterManually.setPadding(textSizeConverter.getPaddingORMarginValue(6), 0, textSizeConverter.getPaddingORMarginValue(6), 0);
    }

    private static final long DELAY_MILLIS = 15000;
    private CountDownTimer cTimer = null;

    private void startTimer() {
        cTimer = new CountDownTimer(DELAY_MILLIS, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (isFrontView) {
                } else if (Utility.getInstance().getPassportNumber() == null &&
                        Utility.getInstance().getDateOfBirth() == null &&
                        Utility.getInstance().getExpiryDate() == null) {
                } else {
                    cTimer.start();
                }

            }

        }.start();
       /* handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRescanningDocumentVisibility();
            }
        }, DELAY_MILLIS);*/
    }

    private void initView() {

        selectedDocType = getIntent().getStringExtra(getResources().getString(R.string.doc_key));
        instruction = getIntent().getBooleanExtra("instruction", false);
        // Change status bar to transparent and set dark icons
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setLayoutAndTextSize();
        binding.overlay.setZOrderMediaOverlay(true);
        holder = binding.overlay.getHolder();
        holder.setFormat(PixelFormat.TRANSPARENT);
        holder.addCallback(this);

        new ViewModelProvider(this, (ViewModelProvider.Factory) ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())).get(CameraXViewModel.class).getProcessCameraProvider().observe(this, provider -> {
            cameraProvider = provider;
            bindAllCameraUseCases();
        });
        options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                        .build();
        scanner = BarcodeScanning.getClient();

        startTimer();
        validateUser();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        // ImageAnalysis

    }

    private void validateUser() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bindAnalysisUseCase();
            }
        }, 2000);
    }


    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider.unbindAll();

            // Image capture use case
            bindPreviewUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder().setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation()).build();

        cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, previewUseCase);
    }


    private void bindAnalysisUseCase() {
        try {

            if (cameraProvider == null) {
                return;
            }

            if (analysisUseCase != null) {
                cameraProvider.unbind(analysisUseCase);
            }

            ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
            Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
            if (targetResolution != null) {
                builder.setTargetResolution(targetResolution);
            }

            analysisUseCase = new ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
            analysisUseCase.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
                @SuppressLint("UnsafeOptInUsageError")
                @Override
                public void analyze(@NonNull ImageProxy pImage) {
                    //                create a crop bitmap of square area of image
                    @SuppressLint("UnsafeOptInUsageError")
                    Image mediaImage = pImage.getImage();
                    logger.log("barcode value:");
                    if (mediaImage != null) {
                        //                    Log.d(TAG, "analyze: image");
                        InputImage image = InputImage.fromMediaImage(mediaImage, pImage.getImageInfo().getRotationDegrees());

//                    logger.log("barcode value:");
                        scanner.process(image)
                                .addOnSuccessListener(barcodes -> {
//                                logger.log("barcode value:"+barcodes);
                                    if (barcodes.size() == 0) {
                                        pImage.close();
                                    } else {
                                        for (Barcode barcode : barcodes) {
                                            // Handle the scanned QR code result here
                                            String scannedData = barcode.getRawValue();
                                            logger.log("barcode value:" + scannedData + "  " + barcode.getValueType());
                                            if (scannedData != null) {
                                                callValidateScanCode(scannedData);
                                            } else {
                                                pImage.close();
                                            }
                                            // Process the scanned data as needed
                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    pImage.close();
                                    // Handle any errors
                                });
                    }
                }
            });
            cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
        }catch (Exception e){

        }
    }

    private boolean apicalling = false;

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }


    private void captureAnImage() {

        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
            }

            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                super.onCaptureSuccess(image);
            }
        });

    }
    private void callValidateScanCode(String code) {
        LoaderUtil.showLoader(QRImplementationActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
        SharedPreferences securePreferences = new SecurePreferences(this);
        String companyUUID=securePreferences.getString(getString(R.string.company_uuid),"");
        String url=securePreferences.getString(companyUUID+getString(R.string.backend_base_url), "");
        /*if (BuildConfig.FLAVOR.equals("dev")){
            url= new AllUrls().getDevMainURL();
        }else{
            url= new AllUrls().getProdMainURL();
        }*/
        logger.log("validating code: " + url + "    " +code+"   "+ securePreferences.getString(getString(R.string.register_token), ""));
        APIClient.getClient(url,  securePreferences.getString(getString(R.string.register_token), "")).create(APIInterface.class).callValidateScanCode(code).
                enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        LoaderUtil.hideLoader(QRImplementationActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        logger.log("validating code: "+response.isSuccessful());
                        if (response.isSuccessful()) {
                            try {
                                String jsonResponse = response.body().string();
                                JSONObject jsonObject = new JSONObject(jsonResponse);
                                boolean success = jsonObject.getBoolean("success");
                                logger.log("response---: " + jsonResponse+"\n"+success);
                                if (success) {
                                    securePreferences.edit().putString(getString(R.string.scan_code), code).apply();

                                    if (instruction) {
                                        Intent i = new Intent(QRImplementationActivity.this, InstructionScreenActivity.class);
                                        i.putExtra(getResources().getString(R.string.doc_key), selectedDocType);
                                        startActivity(i);
                                    } else {
                                        Intent i = new Intent(QRImplementationActivity.this, StartScanningActivity.class);
                                        i.putExtra(getResources().getString(R.string.doc_key), selectedDocType);
                                        startActivity(i);
                                    }
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else {
                                    Toast.makeText(getApplicationContext(),"This scan code has already been taken",Toast.LENGTH_LONG).show();
                                }
                                // Now you have the raw JSON response as a string (jsonResponse)
                                // You can parse it or work with it as needed.
                            } catch (IOException e) {
                                logger.log("response:exception " + e);
                                e.printStackTrace();
                            } catch (JSONException e) {
                                logger.log("response:jsonexception " + e);
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        logger.log("response:jsonexception " + t);
                        LoaderUtil.hideLoader(QRImplementationActivity.this, binding.customLoader.loaderContainer, textSizeConverter);
                        // Handle the case where the API call failed (e.g., network issues)
                    }

                });
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        DrawFocusRect(getResources().getColor(R.color.barcode_back));
    }

    private void DrawFocusRect(int color) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = binding.previewView.getHeight();
        int width = binding.previewView.getWidth();
        diameter = width;
        if (height < width) {
            diameter = height;
        }

        // (0.06 * diameter)
        int offset = (int) (0.14 * diameter);
        diameter -= offset;

        canvas = holder.lockCanvas();
        canvas.drawColor(getResources().getColor(R.color.barcode_back), PorterDuff.Mode.ADD);
        //border's properties
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.TRANSPARENT);
        paint.setStrokeWidth(3);

        left = width - diameter;
        top = (int) (height / screenDivideValue - diameter / 1.8);
        right = diameter;
        bottom = (int) (height / screenDivideValue + diameter / 3.9);

        boxHeight = bottom - top;
        boxWidth = right - left;

        Paint paint1 = new Paint(Paint.DITHER_FLAG);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(Color.TRANSPARENT);
        paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Paint paint2 = new Paint();
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(Color.BLACK);
        paint2.setStrokeWidth(5);

        int corner = 30;
        int cornerSpace = 0;
        int increase = 30;
        paint.setStrokeJoin(Paint.Join.ROUND);
//        canvas.drawPath(createCornersPath(left - cornerSpace, top - cornerSpace, right + cornerSpace, bottom + cornerSpace), paint2);

//        canvas.drawRect(left, top, right, bottom, paint1);
        canvas.drawRoundRect(new RectF(left, top, right, bottom), corner, corner, paint1);
        //Changing the value of x in diameter/x will change the size of the box ; inversely proportionate to x

        canvas.drawRoundRect(new RectF(left, top, right, bottom), corner, corner, paint);
        createCornersPath(left - cornerSpace, top - cornerSpace, right + cornerSpace, bottom + cornerSpace, paint2);
        holder.unlockCanvasAndPost(canvas);

    }

    private Path createCornersPath(int left, int top, int right, int bottom, Paint paint) {
        Path path = new Path();
        float radius = 30; // Replace with your desired radius value
        float lengthF = 80; // Replace with your desired length value

// Top Left to right
        canvas.drawLine(left + radius, top, left + lengthF, top, paint);
// Top Left to bottom
        canvas.drawLine(left, top + radius, left, top + lengthF, paint);
        RectF rectTL = new RectF(left, top, left + (radius * 2), top + (radius * 2));
        canvas.drawArc(rectTL, 180f, 90f, false, paint);

// Top Right to left
        canvas.drawLine(right - radius, top, right - lengthF, top, paint);
// Top Right to Bottom
        canvas.drawLine(right, top + radius, right, top + lengthF, paint);
        RectF rectTR = new RectF(right - (radius * 2), top, right, top + (radius * 2));
        canvas.drawArc(rectTR, 270f, 90f, false, paint);

// Bottom Left to right
        canvas.drawLine(left + radius, bottom, left + lengthF, bottom, paint);
// Bottom Left to top
        canvas.drawLine(left, bottom - radius, left, bottom - lengthF, paint);
        RectF rectBL = new RectF(left, bottom - (radius * 2), left + (radius * 2), bottom);
        canvas.drawArc(rectBL, 90f, 90f, false, paint);

// Bottom right to left
        canvas.drawLine(right - radius, bottom, right - lengthF, bottom, paint);
// Bottom right to top
        canvas.drawLine(right, bottom - radius, right, bottom - lengthF, paint);
        RectF rectBR = new RectF(right - (radius * 2), bottom - (radius * 2), right, bottom);
        canvas.drawArc(rectBR, 0f, 90f, false, paint);


        return path;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

