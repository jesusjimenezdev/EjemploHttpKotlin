package jesus.net.ejemplojsonkotlin

import android.os.AsyncTask
import android.os.StrictMode
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DescargaUrl(var completadoListener: CompletadoListener?): AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String): String? {
        try {
            return descargarDatos(params[0])
        }catch (e: IOException) {
            return null
        }
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        try {
            completadoListener?.descargaCompleta(result)
        }catch (e: Exception) {

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

}