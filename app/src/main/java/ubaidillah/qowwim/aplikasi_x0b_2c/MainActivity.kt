package ubaidillah.qowwim.aplikasi_x0b_2c

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.lang.Exception
//import java.util.jar.Manifest
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnCamera -> {
                requestPermissions()
            }
            R.id.btnSend -> {
                uploadFile()
            }
        }
    }
    val root = "http://192.168.43.216"
    val url = "$root/Qowwim/AndroidPertemuan15/upload.php"
    var imstr=""
    var namafile=""
    var fileUri = Uri.parse("")
    lateinit var mediaHelper: MediaHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try{
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
            m.invoke(null)
        }catch (e:Exception){
            e.printStackTrace()
        }
        mediaHelper = MediaHelper()
        btnCamera.setOnClickListener(this)
        btnSend.setOnClickListener(this)
    }
    fun uploadFile(){
        val request = object : StringRequest(Method.POST,url,
        Response.Listener { response ->
            val jsonObject = JSONObject(response);
            val kode = jsonObject.getString("kode")
            if(kode.equals("000")){
                Toast.makeText(this,"Upload Foto Sukses", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Upload Foto Gagal", Toast.LENGTH_SHORT).show()
            }
        },
        Response.ErrorListener { error ->
            Toast.makeText(this, "Tidak dapat Terhubung ke Server", Toast.LENGTH_SHORT).show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val hm = HashMap<String, String>()
                hm.put("imstr",imstr)
                hm.put("namafile",namafile)
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }

    fun requestPermissions()= runWithPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
    Manifest.permission.CAMERA){
        fileUri = mediaHelper.getOutputMediaFileUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri)
        startActivityForResult(intent,mediaHelper.getRcCamera())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK)
            if(requestCode == mediaHelper.getRcCamera()){
                imstr = mediaHelper.getBitmapToString(imv,fileUri)
                namafile = mediaHelper.getMyFileName()
        }
    }

}