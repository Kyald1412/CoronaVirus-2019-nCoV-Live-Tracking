package co.kyald.coronavirustracking.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class Utils {

    fun aboutAlert(context: Context, title: String, message: String) {

        val builder = AlertDialog.Builder(context)

        with(builder)
        {
            setTitle("About")
            setMessage("Hello, got any ideas?, don't hesitate to contact me \n\ndhikyaldwiansyah@gmail.com\n\nData scraped from:\n- infographics.channelnewsasia.com\n- JHU CSSE")
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            show()
        }

    }

}