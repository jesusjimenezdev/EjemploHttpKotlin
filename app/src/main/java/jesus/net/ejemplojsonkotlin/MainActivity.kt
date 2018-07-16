package jesus.net.ejemplojsonkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), CompletadoListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnChekRed.setOnClickListener {
            // codigo para validar si hay internet
            if(NetWork.comprobarInternet(this)){
                Toast.makeText(this,"Si hay internet", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"No hay internet", Toast.LENGTH_LONG).show()
            }
        }

        btnSolicitudHttp.setOnClickListener {
            if(NetWork.comprobarInternet(this)){
                descargarDatos("https://www.asociacionaepi.es")
                //Log.d("SOLICITUD ONCLIK", descargarDatos("https://www.asociacionaepi.es"))
                DescargaUrl(this).execute("https://www.asociacionaepi.es")
            } else {
                Toast.makeText(this,"No hay internet", Toast.LENGTH_LONG).show()
            }
        }

        btnVolley.setOnClickListener {
            if(NetWork.comprobarInternet(this)){
                solicitudHttpVolley("https://www.asociacionaepi.es")
            } else {
                Toast.makeText(this,"No hay internet", Toast.LENGTH_LONG).show()
            }
        }

        btnOkHttp.setOnClickListener {
            if(NetWork.comprobarInternet(this)){
                solicitudHTTPOkHttp("https://www.asociacionaepi.es")
            } else {
                Toast.makeText(this,"No hay internet", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Throws(IOException::class)
    fun descargarDatos(url: String): String {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var inputStream: InputStream? = null
        try {
            val url = URL(url)
            val conexion = url.openConnection() as HttpURLConnection
            conexion.requestMethod = "GET"
            conexion.connect()

            inputStream = conexion.inputStream
            return inputStream.bufferedReader().use {
                it.readText()
            }
        } finally {
            if(inputStream != null) {
                inputStream.close()
            }
        }
    }

    // Metodo de la interfaz
    override fun descargaCompleta(resultado: String) {
        Log.d("DESCARGA COMPLETA", resultado)
    }

    // Metodo para volley, hay que poner una linea en el manifest, si no da error:
    // <uses-library android:name="org.apache.http.legacy" android:required="false"/>
    fun solicitudHttpVolley(url: String) {
        val queue = Volley.newRequestQueue(this)
        val solicitud = StringRequest(Request.Method.GET, url, Response.Listener<String> {
            response ->
            try {
                Log.d("SOLICITUD HTTP VOLLEY", response)
            }catch (e: Exception){

            }
        }, Response.ErrorListener {  })
        queue.add(solicitud)
    }

    // Metodo para OkHttp
    fun solicitudHTTPOkHttp(url: String) {
        val cliente = OkHttpClient()
        val solicitud = okhttp3.Request.Builder().url(url).build()
        cliente.newCall(solicitud).enqueue(object: okhttp3.Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                // implementar error
            }

            override fun onResponse(call: Call?, response: okhttp3.Response?) {
                val resultado = response?.body()?.string()
                this@MainActivity.runOnUiThread {
                    try {
                        Log.d("SOLICITUD HTTP OKHTTP", resultado)
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            }
        })
    }

}
