package com.example.scantocookapp.ui.scan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scantocookapp.R
import com.example.scantocookapp.ml.ModelTflite1
import com.google.firebase.storage.FirebaseStorage
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class ScanActivity : AppCompatActivity() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_setting)
//        // Your setting activity initialization code here
//        supportActionBar?.hide()
//    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ScanActivity::class.java)
        }
    }


    // Declaring variables
    private lateinit var takePhoto: Button
    private lateinit var choosePhoto: Button
    private lateinit var result: TextView
    private lateinit var GotoUrlGoggle: TextView
    private lateinit var image: ImageView
    private lateinit var predict: Button
    private lateinit var bitmap: Bitmap

    // Image size
    private val imageSize = 299


    // Permission request code
    private val permissionRequestCode = 1001


    // override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        takePhoto = findViewById(R.id.button4)
        choosePhoto = findViewById(R.id.button5)
        result = findViewById(R.id.textView2)
        image = findViewById(R.id.imageView7)
        predict = findViewById(R.id.button)

        // Arahkan textview id dibawah ini ke url google, jika sudah di classification dan memunculkan label dan sesuaikan dengan prediction yang sudah di classification
        GotoUrlGoggle = findViewById(R.id.Gotourlgoogle)

        val labels = listOf("gudeg", "nasi goreng", "pempek", "rendang", "sate")
//        val urls = listOf(
//            "https://www.google.com/search?q=gudeg",
//            "https://www.google.com/search?q=nasi+goreng",
//            "https://www.google.com/search?q=pempek",
//            "https://www.google.com/search?q=rendang",
//            "https://www.google.com/search?q=sate"
//        )
        supportActionBar?.hide()
        // Image processor
        val imageProcessor = ImageProcessor.Builder().add(ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.BILINEAR)).build()

        takePhoto.setOnClickListener {
            if (checkCameraPermission()) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 200)
            } else {
                requestCameraPermission()
            }
        }


        choosePhoto.setOnClickListener {
            if (checkReadStoragePermission()) {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                // Handle null point
                startActivityForResult(Intent.createChooser(intent, "Select an Image"), 100)
            } else {
                requestReadStoragePermission()
            }
        }

        predict.setOnClickListener {
            // Check whether image is selected or not
            if (!::bitmap.isInitialized) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)

            val processedImage = imageProcessor.process(tensorImage)

            val model = ModelTflite1.newInstance(this)

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(convertBitmapToByteBuffer(processedImage.bitmap))

            // Run model inference and get the result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            var maxIdx = 0
            outputFeature0.forEachIndexed { index, fl ->
                if (outputFeature0[maxIdx] < fl) {
                    maxIdx = index
                }
            }
            if (maxIdx >= labels.size) {
                Toast.makeText(this, "Invalid classification result", Toast.LENGTH_SHORT).show()
            } else {
                val classificationResult = labels[maxIdx]
                result.text = classificationResult

                GotoUrlGoggle.setOnClickListener {
                    val classificationResult = result.text.toString()
                    openGoogleSearchUrl(classificationResult)
                }
                // Simpan prediksi dan gambar ke Firebase
                savePredictionToFirebase(classificationResult)
                saveDataToFirebase(bitmap)
                model.close()
            }
        }
    }
    // Subproblem 1: Requesting camera permission
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            permissionRequestCode
        )
    }

    // Subproblem 2: Requesting read storage permission
    private fun checkReadStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestReadStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            permissionRequestCode
        )
    }

    private fun savePredictionToFirebase(prediction: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "prediction_$currentDate.txt"
        val predictionRef = storageRef.child("predictions/$fileName")

        val predictionData = prediction.toByteArray()

        val uploadTask = predictionRef.putBytes(predictionData)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Prediction uploaded to Firebase", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to upload prediction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveDataToFirebase(bitmap: Bitmap) {
        val storageRef = FirebaseStorage.getInstance().reference
        val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "image_$currentDate.jpg"
        val imageRef = storageRef.child("images/$fileName")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageData = baos.toByteArray()

        val uploadTask = imageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Image uploaded to Firebase", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGoogleSearchUrl(classificationResult: String) {
        val labels = listOf("gudeg", "nasi goreng", "pempek", "rendang", "sate")
        val urls = listOf(
            "https://www.google.com/search?q=gudeg",
            "https://www.google.com/search?q=nasi+goreng",
            "https://www.google.com/search?q=pempek",
            "https://www.google.com/search?q=rendang",
            "https://www.google.com/search?q=sate"
        )

        val index = labels.indexOf(classificationResult)
        if (index != -1) {
            val url = urls[index]
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK) {
            val uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            image.setImageBitmap(bitmap)
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            bitmap = data?.extras?.get("data") as Bitmap
            image.setImageBitmap(bitmap)
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]

                byteBuffer.putFloat(((value shr 32) and 0xFF) / 255.0f)
                byteBuffer.putFloat(((value shr 8) and 0xFF) / 255.0f)
                byteBuffer.putFloat((value and 0xFF) / 255.0f)
            }
        }

        return byteBuffer
    }
}

