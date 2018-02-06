package biz.riverone.jancodemaker

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * バーコードラベル表示ダイアログ
 * Created by kawahara on 2018/02/06.
 */
class BarcodeLabelFragment : DialogFragment() {

    companion object {
        private const val ARG_KEY_BARCODE_STRING = "barcode_string"
        private const val ARG_KEY_WIDTH = "barcode_width"
        private const val ARG_KEY_HEIGHT = "barcode_height"

        fun show(manager: FragmentManager, barcodeString: String, width: Int, height: Int) {
            val dialog = BarcodeLabelFragment()
            val arg = Bundle()
            arg.putString(ARG_KEY_BARCODE_STRING, barcodeString)
            arg.putInt(ARG_KEY_WIDTH, width)
            arg.putInt(ARG_KEY_HEIGHT, height)

            dialog.arguments = arg
            dialog.show(manager, "Dialog")
        }
    }

    private var container: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.container = container
        return null
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.fragment_barcode_label, container, false)
        val themeContext = ContextThemeWrapper(context, R.style.AppTheme)

        val builder = AlertDialog.Builder(themeContext)
        //builder.setTitle(resources.getString(dialogTitleResourceId))

        if (arguments != null && arguments.containsKey(ARG_KEY_BARCODE_STRING)) {
            val barcodeString = arguments.getString(ARG_KEY_BARCODE_STRING, "")

            val width = arguments.getInt(ARG_KEY_WIDTH, 600)
            val height = arguments.getInt(ARG_KEY_HEIGHT, 200)

            // キャプションを表示する
            val textViewNumber = view.findViewById<TextView>(R.id.textViewNumber)
            textViewNumber.text = barcodeString

            // バーコード画像を表示する
            val imageViewJan = view.findViewById<ImageView>(R.id.imageViewJan)
            val bitmap = JanImage.toJanImage(barcodeString, width, height)
            imageViewJan.setImageBitmap(bitmap)
        }

        val labelLayout = view.findViewById<View>(R.id.labelLayout)
        labelLayout.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create()
    }

}