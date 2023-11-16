package com.scada.smartfarm;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.scada.smartfarm.utils.Constans.PERMISSION_CODE_OPEN_CAMERA;
import static com.scada.smartfarm.utils.Constans.PERMISSION_CODE_READ_GALLERY;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
//import com.jjoe64.graphview.GraphView;
//import com.jjoe64.graphview.series.DataPoint;
//import com.jjoe64.graphview.series.LineGraphSeries;
import com.scada.smartfarm.api.APIClient;
import com.scada.smartfarm.api.APIInterface;
import com.scada.smartfarm.databinding.ActivityMainBinding;
import com.scada.smartfarm.response.BoxesDataItem;
import com.scada.smartfarm.response.ResponseFarms;
import com.scada.smartfarm.response.Temperature;
import com.scada.smartfarm.utils.LocationTracking;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private ActivityMainBinding activityMainBinding2;
    private Dialog dialog;
    private Uri image_uri;
    private String mImageFileLocation = "";
    private String raplace = "";
    protected ArrayList<ResponseFarms> list;
    protected ArrayList<Integer> listAbsoultePost, listAbsoultePost2;
    protected ArrayList<Double> listrelativPost;
    protected ArrayList<Double> listBeans;
    protected ArrayList<Double> listHist;
    private LineChart mChart;

    //20221130
    private SpcImageParameter param;
//    private String temperatureMin = "";
//    private String temperatureMax = "";
//    private String imageLocation;

    //20221220
    LocationTracking locationTracking;
    private final ArrayList<String> permissions = new ArrayList();
    private ArrayList<String> permissionsToRequest;
    private final static int ALL_PERMISSIONS_RESULT = 101;

    APIInterface apiInterface;
    Bitmap bitmap;
    Bitmap rotateBitmap;

    Integer abs;
    Double abs1, binsItem, hinsItem;


    //20221130
    private void checkDataReady() {
        param = new SpcImageParameter(activityMainBinding, mImageFileLocation);
        boolean isReady = param.checkDataReady();
        activityMainBinding.btnAnalyze.setEnabled(isReady);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = activityMainBinding.getRoot();

        list = new ArrayList<>();
        listAbsoultePost = new ArrayList<>();
        listAbsoultePost2 = new ArrayList<>();
        listrelativPost = new ArrayList<Double>();
        listHist = new ArrayList<Double>();
        listBeans = new ArrayList<Double>();

        setContentView(view);
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }
        setContentView(view);

        dialog = new Dialog(MainActivity.this);

        //20221220
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        locationTracking = new LocationTracking(getApplicationContext());
        ceklocation(locationTracking);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        if (locationTracking.canGetLocation()) {
            double longitude = locationTracking.getLongitude();
            double latitude = locationTracking.getLatitude();
            Log.d("Body latitude", "latitude val" + latitude);
            Log.d("Body longitudesz", "latitude val" + longitude);
            activityMainBinding.latitude.setText(String.valueOf(latitude));
            activityMainBinding.longitude.setText(String.valueOf(longitude));
        } else {

             locationTracking.showSettingsAlert(getApplicationContext());
        }



        //TAG 1 : 2022 12 02  [TEST] MP Android Chart

        /*
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = " Smart Farm";
        ArrayList<String> xAxisValues = new ArrayList<String>();


        xAxisValues.add("2001");
        xAxisValues.add("2002");
        xAxisValues.add("2003");
        xAxisValues.add("2004");
        xAxisValues.add("2005");
        xAxisValues.add("2006");
        xAxisValues.add("2007");
        xAxisValues.add("2008");
        xAxisValues.add("2009");
        xAxisValues.add("2010");
        xAxisValues.add("2011");
        xAxisValues.add("2012");
        xAxisValues.add("2013");
        xAxisValues.add("2014");
        xAxisValues.add("2015");


        activityMainBinding.barChart2.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

        //input data chart graphic 0...60.1,50.1
        for (int i = 0; i < 6; i++) {
            valueList.add(i * 100.1);
        }

        valueList.add(20.1);
        valueList.add(30.1);
        valueList.add(50.1);
        valueList.add(30.1);
        valueList.add(60.1);
        valueList.add(60.1);
        valueList.add(60.1);
        valueList.add(60.1);
        valueList.add(65.1);
        valueList.add(60.1);
        valueList.add(55.1);
        valueList.add(60.1);
        valueList.add(50.1);

        //fit the data into a bar
        for (int p = 0; p < valueList.size(); p++) {
            BarEntry barEntry = new BarEntry(p, valueList.get(p).floatValue());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        BarData data = new BarData(barDataSet);
        activityMainBinding.barChart2.setData(data);
        activityMainBinding.barChart2.invalidate();
        // barChart.animateXY(2000, 2000);
        // llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);


        activityMainBinding.barChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //END TAG : 2022 02 12 [TEST] MP Android Chart

        */


        //TAG 2 : 2022 12 05 [TEST] MP Android Chart

        /*BarData data = new BarData( getDataSet());
        activityMainBinding.barChart2.setData(data);
        activityMainBinding.barChart2.animateXY(2000, 2000);
        activityMainBinding.barChart2.invalidate();*/


        //TAG 3 : 2022 12 05 [TEST] MP Android Chart
        /*initBarChart();
        showBarChart();
        */


        activityMainBinding.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //20221130
                selectImage();

            }
        });

        //20221130
        activityMainBinding.minInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //int coba = 1;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //int coba = 1;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkDataReady();
            }
        });

        //20221130
        activityMainBinding.maxInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //int coba = 1;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //int coba = 1;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                checkDataReady();
            }
        });

    }

    private void ceklocation(LocationTracking locationTracking) {

        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        locationTracking = new LocationTracking(getApplicationContext());

        if (locationTracking.canGetLocation()) {
            double longitude = this.locationTracking.getLongitude();
            double latitude = this.locationTracking.getLatitude();
            Log.d("Body Longitude", "longitude : " + longitude + "& " + latitude);
        } else {

        }
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (ContextCompat.checkSelfPermission(getApplicationContext(), permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private void initBarChart() {
        //hiding the grey background of the chart, default false if not set
        activityMainBinding.barChart2.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        activityMainBinding.barChart2.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        activityMainBinding.barChart2.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        activityMainBinding.barChart2.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        activityMainBinding.barChart2.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        activityMainBinding.barChart2.animateX(1000);

        XAxis xAxis = activityMainBinding.barChart2.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = activityMainBinding.barChart2.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = activityMainBinding.barChart2.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = activityMainBinding.barChart2.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false);

    }

    private void showBarChart() {

        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = "Title";

        //input data
        for (int i = 0; i < 6; i++) {
            valueList.add(i * 100.1);
        }

        //fit the data into a bar
        for (int i = 1; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);
        initBarDataSet(barDataSet);

        BarData data = new BarData(barDataSet);

        activityMainBinding.barChart2.setData(data);
        activityMainBinding.barChart2.invalidate();


    }

    private void initBarDataSet(BarDataSet barDataSet) {
        //Changing the color of the bar
        barDataSet.setColor(Color.parseColor("#304567"));
        //Setting the size of the form in the legend
        barDataSet.setFormSize(15f);
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(false);
        //setting the text size of the value of the bar
        barDataSet.setValueTextSize(12f);
    }

    private List<IBarDataSet> getDataSet() {
        ArrayList dataSets = null;

        ArrayList valueSet1 = new ArrayList();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);

        ArrayList valueSet2 = new ArrayList();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private void selectImage() {
        dialog.setTitle("Image");
        dialog.setContentView(R.layout.dialog_custom_design);
        TextView gallery = dialog.findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE_READ_GALLERY);
                    }
                } else {
                    //permission already granted
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                    dialog.hide();

                }
            }
        });

        TextView cammera2 = dialog.findViewById(R.id.cammera2);
        cammera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        String[] permision = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permision, PERMISSION_CODE_OPEN_CAMERA);
                    } else {
                        //permission already granted
                        openCamera();
                    }
                } else {
                    // system OS < Marshmallow
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    openCamera();
                }
            }
        });

        dialog.show();
    }

    private void openCamera() {
        dialog.hide();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        //image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        image_uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);

        startActivityForResult(cameraIntent, 1);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMAGE_FARM_" + timeStamp + "_";
        boolean isEmulated = Environment.isExternalStorageEmulated();

        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDirectory);
        mImageFileLocation = image.getAbsolutePath();

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, do something you want
                } else {
                    // permission denied
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(mImageFileLocation, bitmapOptions);

                    rotateImage(setReducedImageSize());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                mImageFileLocation = picturePath;

                //20221130
                checkDataReady();

                c.close();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4; // InSampleSize = 4;

                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    activityMainBinding.viewImage.setImageBitmap(bitmap);

                    activityMainBinding.btnAnalyze.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //20221130
//                                uploadImage(bitmap, activityMainBinding.minInput.getText().toString(), activityMainBinding.maxInput.getText().toString());
                            //20221130
//                                uploadImage(bitmap, min, max);
                            uploadImage(bitmap, param);


                        }
                    });


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void rotateImage(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(mImageFileLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


        activityMainBinding.viewImage.setImageBitmap(rotateBitmap);


        if (activityMainBinding.minInput.getText().toString().matches("") && activityMainBinding.maxInput.toString().matches("")) {
            Toast.makeText(this, "ANGKA MINIMAL/ MAKSIMAL KOSONG, SILAHKAN ISI LALU UPLOAD GAMBAR KEMBALI", Toast.LENGTH_SHORT).show();
            return;
        } else {
            activityMainBinding.btnAnalyze.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //20221130
                    uploadImage(rotateBitmap, param);

                }
            });
        }


    }

    private Bitmap setReducedImageSize() {
        int targetImageViewWidth = activityMainBinding.viewImage.getWidth();
        int targetImageViewHeight = activityMainBinding.viewImage.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth / targetImageViewWidth, cameraImageHeight / targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        //Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        //mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmap);
        Bitmap compressedOri = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);

        Bitmap compressed = null;
        //save bitmap to byte array

        try {
            File file = new File(mImageFileLocation);
            FileOutputStream fOut = new FileOutputStream(file);
            compressedOri.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return compressedOri;
    }

    //20221130
//    private void uploadImage(Bitmap rotateBitmap, String min, String max) {
    private void uploadImage(Bitmap rotateBitmap, SpcImageParameter param) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading Image...");
        pd.setCancelable(false);
        pd.show();

        File file = new File(mImageFileLocation);
        int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));

        apiInterface = APIClient.getClient().create(APIInterface.class);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part bodyPaarts = MultipartBody.Part.createFormData("image", file.getName(), requestFile);


        //20221130
        String min = String.valueOf(param.temperatureMin);
        String max = String.valueOf(param.temperatureMax);
        RequestBody req2 = RequestBody.create(MediaType.parse("text/plain"), min);
        RequestBody req3 = RequestBody.create(MediaType.parse("text/plain"), max);

        Call<ResponseFarms> call = apiInterface.callResponseFarm(bodyPaarts, req2, req3);
        call.enqueue(new Callback<ResponseFarms>() {
            @Override
            public void onResponse(Call<ResponseFarms> call, Response<ResponseFarms> response) {


                if (response.code() == 200) {
                    ResponseFarms responseFarms = response.body();
                    List<BoxesDataItem> boxData = responseFarms.getBoxData().getBoxesData();



                    String strImage = responseFarms.getImage();
                    String[] strs = strImage.split("'");
                    String myImage = strs[1];
//                    StringBuffer sb = new StringBuffer();
//                    sb.append(image);

                    String message = "";
                    try {
                        byte[] decodedString = Base64.decode(myImage, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        activityMainBinding.imageGenerateResult.setImageBitmap(bmp);

                    }catch (Exception e){
                        message = e.toString();
                    }



//                    Bitmap bitmap1 = getBitmapFromString(sb);


                    int boxDataSize = responseFarms.getBoxData().getBoxesData().size();
                    ArrayList<Double> valueList = new ArrayList<Double>();
                    ArrayList<BarEntry> entries = new ArrayList<>();
                    ArrayList<BarEntry> entries2 = new ArrayList<>();
                    String title = " Smart Farm";
                    ArrayList<String> xAxisValues = new ArrayList<String>();
                    activityMainBinding.barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

                    for (int i = 0; i < boxDataSize; i++) {
                        if (i >= 0 && i < boxDataSize) {
                            Temperature temp = boxData.get(i).getTemperature();
                            SpcTemperatureCollection tempCol = new SpcTemperatureCollection(temp);
                            List<SpcTemperature> listTemperature = tempCol.listTemperature;
                            int count = listTemperature.size();
                            if (i == 0) {
                                for (int j = 0; j < count; j++) {
                                    SpcTemperature temperature = listTemperature.get(j);

                                    BarEntry barEntry = new BarEntry((float) temperature.temperature.floatValue(), (float) temperature.percentage.floatValue());
                                    entries.add(barEntry);
                                }
                                BarDataSet barDataSet = new BarDataSet(entries, title);
                                BarData data = new BarData(barDataSet);
                                activityMainBinding.barChart.setData(data);
                                activityMainBinding.barChart.invalidate();
                                activityMainBinding.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                            } else if (i == 1) {
                                for (int k = 0; k < count; k++) {
                                    SpcTemperature temperature2 = listTemperature.get(k);

                                    BarEntry barEntry2 = new BarEntry((float) temperature2.temperature.floatValue(), (float) temperature2.percentage.floatValue());
                                    entries2.add(barEntry2);
                                }
                                BarDataSet barDataSet2 = new BarDataSet(entries2, title);
                                BarData data2 = new BarData(barDataSet2);
                                activityMainBinding.barChart2.setData(data2);
                                activityMainBinding.barChart2.invalidate();
                                activityMainBinding.barChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                            } else {

                            }

//                            List<Integer> absolutPath = boxData.get(i).getAbsolutePosition();
//                            List<Double> relativPost = boxData.get(i).getRelativePosition();


//                            for (int j = 0; j < absolutPath.size(); j++) {
//                                abs = absolutPath.get(j);
//                                listAbsoultePost.add(abs);
//                            }
//                            listAbsoultePost2 = listAbsoultePost;
//
//                            for (int j = 0; j < relativPost.size(); j++) {
//                                abs1 = relativPost.get(j);
//                                listrelativPost.add(abs1);
//                            }

                            // Temp

//
//                            List<Double> hist = temp.getHist();
//                            for (int k = 0; k < hist.size(); k++) {
//                                hinsItem = hist.get(k);
//                                listHist.add(hinsItem);
//
//                            }

//                            List<Double> binns = temp.getBins();

//                                binsItem = binns.get(l);
//                                listBeans.add(binsItem);

//                                valueList.add(binsItem);
//                                for (int p = 0; p < valueList.size(); p++) {
//
//                                    BarEntry barEntry = new BarEntry(p, valueList.get(p).floatValue());
//                                    barEntry.setX(list);
//                                    entries.add(barEntry);
//                                }

//                            }
                        }
                    }


                    //fit the data into a bar


                    String gson = new Gson().toJson(responseFarms);
                    raplace = gson;
                    String showStrinRespons = "\tbox_data : [{ '\"'\n\tAbsolute Position\'" + listAbsoultePost2 + "'\'\n\tRelative Position\"" + listrelativPost + "\n\tTemperature: {'\"\n\tBins'\" " + listBeans + " '\"\n\tHist \'" + listHist;
                    raplace.replaceAll("image", "");
                    activityMainBinding.longitude.setText("" + showStrinRespons);
                    list.add(responseFarms);
                    Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();


                    pd.hide();


                } else {
                    Toast.makeText(getApplicationContext(), "Gagal, angka tidak bisa kosong", Toast.LENGTH_LONG).show();
                    pd.hide();

                }
            }

            @Override
            public void onFailure(Call<ResponseFarms> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Gagal, terkoneksi ke api", Toast.LENGTH_LONG).show();
                pd.hide();

            }


        });

    }

    private Bitmap getBitmapFromString(StringBuffer sb) {

        int w = 10, h = 10;

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap

//        Bitmap decodedByte =  null;
        String message = "";
        try {
            byte[] decodedString = Base64.decode(sb.toString(), Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        } catch (Exception e) {
            message = e.getMessage();

        }
        return bmp;

    }

    private void showDataGraph(ArrayList<Double> listHist) {
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = " Avg Obtain Marks";
//        ArrayList<String> xAxisValues = new ArrayList<String>();
//        for (int i = 0 ; i<listBeans.size();i++){
//            Double value = listBeans.get(i);
//            xAxisValues.add(String.valueOf(value));
//        }
//        activityMainBinding.barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
    }

    /*private void showDataHistogram(GhistoryMotorCycle[] gHistory) {

        double x, y;
        for (int i = 0; i < gHistory.length; i++) {



            y = gHistory[i].getMeter();
            x = gHistory[i].getScoreClassfification();
            float c = (float) y;

            YAxis leftAxis = mChart.getAxisLeft();
            leftAxis.removeAllLimitLines();

            leftAxis.setAxisMaximum(300);
            leftAxis.setAxisMinimum(90);
            leftAxis.enableGridDashedLine(c, c, 0f);
            leftAxis.setDrawZeroLine(false);
            leftAxis.setDrawLimitLinesBehindData(false);
            leftAxis.setValueFormatter(new MyValueFormatter());
        }
        mChart.getAxisRight().setEnabled(false);
        setDataHistogram(gHistory);
    }


    private void setDataHistogram(GhistoryMotorCycle[] listHistory) {

        GHistory gHistory = new GHistory();
        ArrayList<Entry> values = new ArrayList<>();
        int historyCount = listHistory.length;
        LineDataSet set1;
        double x;
        double y;
        float f = (float) 50;

        for (int i = 0; i < historyCount; i++) {
            y = listHistory[i].getMeter();
            x = listHistory[i].getScoreClassfification();
            float c = (float) y;
            f = f + 50;
            values.add(new Entry(f, c));
            if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set1.setValues(values);
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
            } else {
                set1 = new LineDataSet(values, "History Data");
                set1.setDrawIcons(false);
                set1.setColor(Color.GREEN);
                set1.setCircleColor(Color.GREEN);
                set1.setLineWidth(1f);
                set1.setCircleRadius(3f);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(9f);
                set1.setDrawFilled(true);
                set1.setFormLineWidth(1f);
                set1.setFormSize(15.f);
                if (Utils.getSDKInt() >= 18) {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.fade_blue);
                    set1.setFillDrawable(drawable);
                } else {
                    set1.setFillColor(Color.GREEN);
                }

            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            LineData data = new LineData(dataSets);
            mChart.setData(data);
        }

    }
    */

}

