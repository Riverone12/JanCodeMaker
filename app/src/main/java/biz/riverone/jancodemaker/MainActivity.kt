package biz.riverone.jancodemaker

import android.content.pm.ActivityInfo
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.text.DecimalFormat

/**
 * JanCodeMaker: JANコードメーカー
 * Copyright (C) 2018 J.Kawahara
 * 2018.2.5 J.Kawahara 新規作成
 * 2018.2.6 J.Kawahara ver.1.00 初版公開
 */
class MainActivity : AppCompatActivity(), ClearComfirmDialog.DialogListener {

    companion object {
        private const val BARCODE_HEIGHT = 200
    }

    private val editPrefix by lazy { findViewById<EditText>(R.id.editTextPrefix) }
    private val editNumber by lazy { findViewById<EditText>(R.id.editTextNumber) }
    private val textViewCheckDigit by lazy { findViewById<TextView>(R.id.textViewCheckDigit) }

    private val barcodeListView by lazy { findViewById<RecyclerView>(R.id.barcodeListView) }
    private lateinit var barcodeListAdapter: JanListAdapter

    private fun getDisplayWidth(): Int {
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        return point.x
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initializeControls()

        // バーコード表示コントロールの準備
        val barcodeWidth = (getDisplayWidth() * 0.75).toInt()
        val barcodeHeight = BARCODE_HEIGHT

        barcodeListView.layoutManager = LinearLayoutManager(this)
        barcodeListAdapter = object :JanListAdapter() {
            override fun onClicked(barcodeString: String) {
                BarcodeLabelFragment.show(
                        supportFragmentManager,
                        barcodeString,
                        barcodeWidth,
                        barcodeHeight)
            }
        }
        barcodeListAdapter.barcodeWidth = barcodeWidth
        barcodeListAdapter.barcodeHeight = barcodeHeight
        barcodeListView.adapter = barcodeListAdapter

        // スワイプで要素を削除する準備
        val touchHelper = ItemTouchHelper(swipeListCallback)
        touchHelper.attachToRecyclerView(barcodeListView)

        // 初期データを表示する
        loadBarcodeList()

        // AdMob
        MobileAds.initialize(applicationContext, "ca-app-pub-1882812461462801~6109110371")
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()

        // 前回終了時の情報を取得する
        AppPreference.initialize(applicationContext)
        editPrefix.setText(AppPreference.lastPrefixString)
        editNumber.setText(AppPreference.lastNumberString)

        barcodeListAdapter.fromJson(AppPreference.historyJson)
    }

    override fun onPause() {
        super.onPause()

        // 終了時の情報を保存する
        AppPreference.lastPrefixString = editPrefix.text.toString()
        AppPreference.lastNumberString = editNumber.text.toString()

        AppPreference.historyJson = barcodeListAdapter.toJson()

        AppPreference.saveAll(applicationContext)
    }

    private fun initializeControls() {
        editPrefix.addTextChangedListener(editWatcher)
        editNumber.addTextChangedListener(editWatcher)

        // クリアボタンの準備
        val buttonClear = findViewById<Button>(R.id.buttonClear)
        buttonClear.setOnClickListener {
            editNumber.text.clear()
            editNumber.requestFocus()
        }

        // インクリメントボタンの準備
        val buttonIncrement = findViewById<Button>(R.id.buttonIncrement)
        buttonIncrement.setOnClickListener {
            var nextNumber = txtToNum(editNumber.text.toString()) + 1
            if (nextNumber >= 10000000000) {
                nextNumber = 0
            }
            val format = DecimalFormat("0000000000")
            editNumber.setText(format.format(nextNumber))
            editNumber.requestFocus()
        }

        // 作成ボタンの準備
        val buttonGenerate = findViewById<Button>(R.id.buttonGenerate)
        buttonGenerate.setOnClickListener {
            val prefix = txtToNum(editPrefix.text.toString()).toInt()
            val number = txtToNum(editNumber.text.toString())
            val strBarcode = JanDigit.toJanString(prefix, number)

            addBarcode(strBarcode)
        }
    }

    private fun txtToNum(text: String): Long {
        try {
            if (text.isEmpty()) {
                return 0
            }
            return text.toLong()
        }
        catch (e: NumberFormatException) {
            return 0
        }
    }

    // 入力された数値を監視して、チェックデジットを表示する
    private val editWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        override fun afterTextChanged(s: Editable?) {
            val prefix = txtToNum(editPrefix.text.toString())
            val number = txtToNum(editNumber.text.toString())
            val cd = JanDigit.calcCheckDigit(prefix.toInt(), number)
            textViewCheckDigit.text = cd.toString()
        }
    }

    // リストにバーコードを追加する
    private fun addBarcode(jan: String) {
        barcodeListAdapter.addItem(0, jan)
        barcodeListView.scrollToPosition(0)
    }

    // 初期データを表示する
    private fun loadBarcodeList() {
        addBarcode("491234567890")
        addBarcode("201234567890")
        addBarcode("210987654321")
    }

    // スワイプでリサイクラービューの要素を削除する
    private val swipeListCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            // 横にスワイプされたら要素を削除する
            if (viewHolder != null) {
                val swipedPosition = viewHolder.adapterPosition
                barcodeListAdapter.remove(swipedPosition)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_all_clear -> {
                val confirmDialog = ClearComfirmDialog.create()
                confirmDialog.setDialogListener(this)
                confirmDialog.show(supportFragmentManager, "Dialog")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun doPositiveClick() {
        barcodeListAdapter.removeAll()
    }

    override fun doNegativeClick() { }
}
