package biz.riverone.jancodemaker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * JanListAdapter.kt: リサイクルビュー用カスタムアダプタ
 * Created by kawahara on 2018/02/06.
 */
open class JanListAdapter:  RecyclerView.Adapter<JanListAdapter.ViewHolder>() {

    companion object {
        // 履歴を表示する最大件数
        private const val MAX_ITEM_COUNT = 30
    }

    private val janList = ArrayList<String>()

    var barcodeWidth: Int = 600
    var barcodeHeight: Int = 200

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val imageViewJan: ImageView = v.findViewById(R.id.imageViewJan)
        val textViewNumber: TextView = v.findViewById(R.id.textViewNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.jan_list_item, parent, false)

        val viewHolder = ViewHolder(v)

        // クリック時のリスナをセットする
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val barcodeString = janList[position]
            onClicked(barcodeString)
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        // キャプションを表示する
        val janNumber = janList[position]
        holder?.textViewNumber?.text = janNumber

        // バーコード画像を表示する
        val bitmap = JanImage.toJanImage(janNumber, barcodeWidth, barcodeHeight)
        holder?.imageViewJan?.setImageBitmap(bitmap)
    }

    override fun getItemCount(): Int {
        return janList.size
    }

    fun addItem(position: Int, jan: String) {

        while (janList.size >= MAX_ITEM_COUNT) {
            remove(janList.size - 1)
        }
        janList.add(position, jan)
        //notifyDataSetChanged()
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        janList.removeAt(position)
        notifyDataSetChanged()
        //notifyItemInserted(position)
    }

    fun removeAll() {
        janList.clear()
        notifyDataSetChanged()
    }

    open fun onClicked(barcodeString: String) { }

    // Preference に保存するためのJSON文字列に変換する
    fun toJson(): String {
        val jsonArray = JSONArray()
        for (i in janList.indices) {
            try {
                jsonArray.put(i, janList[i])
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return jsonArray.toString()
    }

    // Preference に保存したJSON文字列から要素を復元する
    fun fromJson(jsonString: String) {
        janList.clear()
        try {
            val array = JSONArray(jsonString)
            (0 until array.length()).mapTo(janList) { array.optString(it) }
        }
        catch (e: JSONException) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }
}