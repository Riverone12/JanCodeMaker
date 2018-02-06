package biz.riverone.jancodemaker

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import java.util.*

/**
 * クリア確認ダイアログ
 * Created by kawahara on 2018/02/06.
 */
class ClearComfirmDialog : DialogFragment() {

    private var listener: DialogListener? = null

    interface DialogListener : EventListener {
        fun doPositiveClick()
        fun doNegativeClick()
    }

    companion object {
        fun create(): ClearComfirmDialog {
            return ClearComfirmDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alert = AlertDialog.Builder(activity)
                .setTitle(R.string.title_all_clear)
                .setMessage(R.string.message_all_clear)
                .setPositiveButton(R.string.caption_yes) {
                    _, _ ->
                    listener?.doPositiveClick()
                    dismiss()
                }
                .setNegativeButton(R.string.caption_no) {
                    _, _ ->
                    listener?.doNegativeClick()
                    dismiss()
                }
        return alert.create()
    }

    // リスナーを追加
    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }
/*
    fun removeDialogListener() {
        this.listener = null
    }
*/
}