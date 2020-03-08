package com.barmpas.roadai;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.camera.core.ImageProxy;

import static java.lang.StrictMath.abs;

public class ImageClassificationActivity extends AbstractCameraXActivity<ImageClassificationActivity.AnalysisResult> {

  public static final String INTENT_MODULE_ASSET_NAME = "INTENT_MODULE_ASSET_NAME";
  public static final String INTENT_INFO_VIEW_TYPE = "INTENT_INFO_VIEW_TYPE";

  private static final int INPUT_TENSOR_WIDTH = 224;
  private static final int INPUT_TENSOR_HEIGHT = 224;
  private static final int TOP_K = 2;
  public static final String SCORES_FORMAT = "%.2f";

  private LinearLayout bottomSheetLayout;
  private LinearLayout gestureLayout;
  private BottomSheetBehavior sheetBehavior;
  protected ImageView bottomSheetArrowImageView;
  public CheckBox checkBox_cars, checkBox_red;
  public Button navigateButton;
  public Switch voiceSwitch;
  public static Boolean voiceActivate=false;
  public static Boolean carActivate=true;
  public static Boolean redActivate =true;
  public static TextToSpeech t1;
  public String previous = "";


  static class AnalysisResult {

    private final String[] topNClassNames;
    private final float[] topNScores;
    private final long analysisDuration;
    private final long moduleForwardDuration;

    public AnalysisResult(String[] topNClassNames, float[] topNScores,
                          long moduleForwardDuration, long analysisDuration) {
      this.topNClassNames = topNClassNames;
      this.topNScores = topNScores;
      this.moduleForwardDuration = moduleForwardDuration;
      this.analysisDuration = analysisDuration;
    }
  }

  private boolean mAnalyzeImageErrorState;
  private TextView[] mResultRowViews = new TextView[TOP_K];
  private TextView[] mScoreViews = new TextView[TOP_K];
  private Module mModule;
  private String mModuleAssetName;
  private FloatBuffer mInputTensorBuffer;
  private Tensor mInputTensor;

  @Override
  protected int getContentViewLayoutId() {
    return R.layout.activity_camera;
  }

  @Override
  protected TextureView getCameraPreviewTextureView() {
    return ((ViewStub) findViewById(R.id.image_classification_texture_view_stub))
        .inflate()
        .findViewById(R.id.image_classification_texture_view);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
    gestureLayout = findViewById(R.id.gesture_layout);
    sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
    bottomSheetArrowImageView = findViewById(R.id.bottom_sheet_arrow);

    ViewTreeObserver vto = gestureLayout.getViewTreeObserver();
    vto.addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                  gestureLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                  gestureLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                int height = gestureLayout.getMeasuredHeight();
                sheetBehavior.setPeekHeight(height);
              }
            });

    sheetBehavior.setHideable(false);

    sheetBehavior.setBottomSheetCallback(
            new BottomSheetBehavior.BottomSheetCallback() {
              @Override
              public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                  case BottomSheetBehavior.STATE_HIDDEN:
                    break;
                  case BottomSheetBehavior.STATE_EXPANDED:
                  {
                    bottomSheetArrowImageView.setRotation(90);
                  }
                  break;
                  case BottomSheetBehavior.STATE_COLLAPSED:
                  {
                    bottomSheetArrowImageView.setRotation(270);
                  }
                  break;
                  case BottomSheetBehavior.STATE_DRAGGING:
                    break;
                  case BottomSheetBehavior.STATE_SETTLING:
                    bottomSheetArrowImageView.setRotation(270);
                    break;
                }
              }

              @Override
              public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
            });

    // MY NEW VARIABLES !!!!
    navigateButton = findViewById(R.id.navigate_btn);
    checkBox_cars = findViewById(R.id.cars_check);
    checkBox_red = findViewById(R.id.red_check);
    voiceSwitch  = findViewById(R.id.voice_switch);

    mResultRowViews[0] = findViewById(R.id.detected_item);
    mResultRowViews[1] = findViewById(R.id.detected_item1);

    mScoreViews[0] = findViewById(R.id.detected_item_value);
    mScoreViews[1] = findViewById(R.id.detected_item1_value);

    navigateButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (voiceSwitch.isChecked()){
          voiceActivate = true;
        }else{
          voiceActivate = false;
        }

        if (checkBox_cars.isChecked()){
          carActivate = true;
        }else{
          carActivate = false;
        }

        if (checkBox_red.isChecked()){
          redActivate = true;
        }else{
          redActivate = false;
        }
      }
    });
  }

  @Override
  protected void applyToUiAnalyzeImageResult(AnalysisResult result) {

    final TextView rowView = mResultRowViews[0];
    final TextView scoreView = mScoreViews[0];
    if (!result.topNClassNames[0].equals("Unknown")) {
      double score_display = 100-abs(result.topNScores[0]);
      if (score_display>98) {
        if (result.topNClassNames[0].equals("Car") && score_display==100) {
          if (carActivate) {
            rowView.setText(result.topNClassNames[0]);
            scoreView.setText(String.format(Locale.US, SCORES_FORMAT,
                    score_display) + "%");
          }else{
            rowView.setText("");
            scoreView.setText("");
          }
        }
        if (result.topNClassNames[0].equals("Red Traffic Light") && score_display>=98) {
          if (redActivate) {
            rowView.setText(result.topNClassNames[0]);
            scoreView.setText(String.format(Locale.US, SCORES_FORMAT,
                    score_display) + "%");
          }else{
            rowView.setText("");
            scoreView.setText("");
          }
        }
      }else{
        rowView.setText("");
        scoreView.setText("");
      }
    }else{
      rowView.setText("");
      scoreView.setText("");
    }


    if (result.topNClassNames[0]!=null) {
      if (voiceActivate && !result.topNClassNames[0].equals(previous)) {
        if ((result.topNClassNames[0].equals("Car") && carActivate) || (result.topNClassNames[0]).equals("Red Traffic Light") && redActivate) {
          Talk(result.topNClassNames[0]);
          previous = result.topNClassNames[0];
        }
      }
    }else{
      previous="";
    }
  }

  //for voice announcements.
  public void Talk (String annoucement){
    t1=new TextToSpeech(ImageClassificationActivity.this, new TextToSpeech.OnInitListener() {
      @Override
      public void onInit(int status) {
        if (status!=TextToSpeech.ERROR){
          t1.setLanguage(Locale.ENGLISH);
          t1.speak(annoucement,TextToSpeech.QUEUE_ADD,null);
        }
      }
    });
  }

  protected String getModuleAssetName() {
    if (!TextUtils.isEmpty(mModuleAssetName)) {
      return mModuleAssetName;
    }
    final String moduleAssetNameFromIntent = getIntent().getStringExtra(INTENT_MODULE_ASSET_NAME);
    mModuleAssetName = !TextUtils.isEmpty(moduleAssetNameFromIntent)
        ? moduleAssetNameFromIntent
        : "resnet18.pt";
    return mModuleAssetName;
  }

  @Override
  protected String getInfoViewAdditionalText() {
    return getModuleAssetName();
  }

  @Override
  @WorkerThread
  @Nullable
  protected AnalysisResult analyzeImage(ImageProxy image, int rotationDegrees) {
    if (mAnalyzeImageErrorState) {
      return null;
    }

    try {
      if (mModule == null) {
        final String moduleFileAbsoluteFilePath = new File(
            Utils.assetFilePath(this, getModuleAssetName())).getAbsolutePath();
        mModule = Module.load(moduleFileAbsoluteFilePath);

        mInputTensorBuffer =
            Tensor.allocateFloatBuffer(3 * INPUT_TENSOR_WIDTH * INPUT_TENSOR_HEIGHT);
        mInputTensor = Tensor.fromBlob(mInputTensorBuffer, new long[]{1, 3, INPUT_TENSOR_HEIGHT, INPUT_TENSOR_WIDTH});
      }

      final long startTime = SystemClock.elapsedRealtime();
      TensorImageUtils.imageYUV420CenterCropToFloatBuffer(
          image.getImage(), rotationDegrees,
          INPUT_TENSOR_WIDTH, INPUT_TENSOR_HEIGHT,
          TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
          TensorImageUtils.TORCHVISION_NORM_STD_RGB,
          mInputTensorBuffer, 0);

      final long moduleForwardStartTime = SystemClock.elapsedRealtime();
      final Tensor outputTensor = mModule.forward(IValue.from(mInputTensor)).toTensor();
      final long moduleForwardDuration = SystemClock.elapsedRealtime() - moduleForwardStartTime;

      final float[] scores = outputTensor.getDataAsFloatArray();
      final int[] ixs = Utils.topK(scores, TOP_K);

      final String[] topKClassNames = new String[TOP_K];
      final float[] topKScores = new float[TOP_K];
      for (int i = 0; i < TOP_K; i++) {
        final int ix = ixs[i];
        topKClassNames[i] = Constants.ROADAI_CLASSES[ix];
        topKScores[i] = scores[ix];
      }
      final long analysisDuration = SystemClock.elapsedRealtime() - startTime;
      return new AnalysisResult(topKClassNames, topKScores, moduleForwardDuration, analysisDuration);
    } catch (Exception e) {
      Log.e(Constants.TAG, "Error during image analysis", e);
      mAnalyzeImageErrorState = true;
      runOnUiThread(() -> {
        if (!isFinishing()) {
          showErrorDialog(v -> ImageClassificationActivity.this.finish());
        }
      });
      return null;
    }
  }

  @Override
  protected int getInfoViewCode() {
    return getIntent().getIntExtra(INTENT_INFO_VIEW_TYPE, -1);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mModule != null) {
      mModule.destroy();
    }
  }
}
