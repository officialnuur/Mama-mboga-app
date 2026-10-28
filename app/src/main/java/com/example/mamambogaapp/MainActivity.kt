package com.example.mamambogaapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

data class ProduceItem(
    val name: String,
    val price: String,
    val unit: String,
    val description: String,
    val category: String, // "Vegetables" or "Fruits"
    val imageRes: Int
)

class ProduceAdapter(context: Context, private var produceList: List<ProduceItem>) :
    ArrayAdapter<ProduceItem>(context, 0, produceList) {

    fun updateList(newList: List<ProduceItem>) {
        produceList = newList
        notifyDataSetChanged()
    }

    override fun getCount(): Int = produceList.size

    override fun getItem(position: Int): ProduceItem? = produceList.getOrNull(position)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.list_item_produce, parent, false)
        val item = getItem(position)

        val ivProduce = view.findViewById<ImageView>(R.id.ivProduce)
        val tvProduceName = view.findViewById<TextView>(R.id.tvProduceName)
        val tvProduceDetails = view.findViewById<TextView>(R.id.tvProduceDetails)
        val tvProduceDescription = view.findViewById<TextView>(R.id.tvProduceDescription)

        if (item != null) {
            ivProduce.setImageResource(item.imageRes)
            tvProduceName.text = item.name
            tvProduceDetails.text = "${item.price} per ${item.unit} • ${item.category}"
            tvProduceDescription.text = item.description
        }
        return view
    }
}

class MainActivity : AppCompatActivity() {

    private var itemCount = 0
    private var totalPrice = 0
    private var currentCategory = "All"
    private var currentSearchQuery = ""
    private var totalOrdersPlaced = 0

    private val allProduceList = listOf(
        ProduceItem("Sukuma Wiki", "KES 30", "bunch", "Freshly harvested farm greens", "Vegetables", R.drawable.spinach),
        ProduceItem("Spinach", "KES 30", "bunch", "Tender green spinach leaves", "Vegetables", R.drawable.spinach),
        ProduceItem("Ripe Tomatoes", "KES 50", "pile (4 pcs)", "Juicy red cooking tomatoes", "Vegetables", R.drawable.tomato),
        ProduceItem("Red Onions", "KES 40", "kg", "Fresh local red onions", "Vegetables", R.drawable.onion),
        ProduceItem("Capsicum (Hoho)", "KES 50", "portion", "Crisp green bell peppers", "Vegetables", R.drawable.tomato),
        ProduceItem("Carrots", "KES 40", "kg", "Sweet crunchy orange carrots", "Vegetables", R.drawable.onion),
        ProduceItem("Potatoes", "KES 90", "kg", "Fresh farm potatoes", "Vegetables", R.drawable.onion),
        ProduceItem("Pineapple", "KES 100", "piece", "Clean and ripe pineapple", "Fruits", R.drawable.pineapple),
        ProduceItem("Mango", "KES 60", "piece", "Sweet and juicy mangoes", "Fruits", R.drawable.mango),
        ProduceItem("Apple", "KES 30", "piece", "Crisp and sweet apples", "Fruits", R.drawable.apple),
        ProduceItem("Orange", "KES 20", "piece", "Freshly squeezed oranges", "Fruits", R.drawable.orange),
        ProduceItem("Watermelon", "KES 150", "piece", "Refreshing sweet watermelons", "Fruits", R.drawable.watermelon),
        ProduceItem("Grapes", "KES 120", "punnet", "Sweet and juicy seedless grapes", "Fruits", R.drawable.grape)
    )

    private val displayedList = mutableListOf<ProduceItem>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.listView)
        val tvCartSummary = findViewById<TextView>(R.id.textView4)
        val btnCheckout = findViewById<Button>(R.id.btncheckout)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val btnInstagram = findViewById<Button>(R.id.btnInstagram)
        val btnFacebook = findViewById<Button>(R.id.btnFacebook)
        val btnDashboard = findViewById<Button>(R.id.btnDashboard)
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnAll = findViewById<Button>(R.id.btnAll)
        val btnVeg = findViewById<Button>(R.id.btnVeg)
        val btnFruit = findViewById<Button>(R.id.btnFruit)

        displayedList.addAll(allProduceList)
        val adapter = ProduceAdapter(this, displayedList)
        listView.adapter = adapter

        fun filterAndDisplay() {
            val filtered = allProduceList.filter { item ->
                val matchesCategory = currentCategory == "All" || item.category.equals(currentCategory, ignoreCase = true)
                val matchesSearch = currentSearchQuery.isBlank() || item.name.contains(currentSearchQuery, ignoreCase = true) || item.description.contains(currentSearchQuery, ignoreCase = true)
                matchesCategory && matchesSearch
            }
            displayedList.clear()
            displayedList.addAll(filtered)
            adapter.updateList(displayedList)
        }

        btnAll.setOnClickListener {
            currentCategory = "All"
            filterAndDisplay()
        }

        btnVeg.setOnClickListener {
            currentCategory = "Vegetables"
            filterAndDisplay()
        }

        btnFruit.setOnClickListener {
            currentCategory = "Fruits"
            filterAndDisplay()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s?.toString() ?: ""
                filterAndDisplay()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnInstagram.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/mamamboga"))
            startActivity(intent)
        }

        btnFacebook.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/mamamboga"))
            startActivity(intent)
        }

        btnDashboard.setOnClickListener {
            val vegCount = allProduceList.count { it.category == "Vegetables" }
            val fruitCount = allProduceList.count { it.category == "Fruits" }
            val statsMessage = """
                📊 MAMA MBOGA STORE STATS
                
                • Total Catalog Products: ${allProduceList.size}
                • Vegetables Available: $vegCount 🥦
                • Fruits Available: $fruitCount 🍎
                • Total Orders Completed: $totalOrdersPlaced 📦
                • Current Basket Items: $itemCount items
                • Current Basket Value: KES $totalPrice
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("Store Analytics & Charts")
                .setMessage(statsMessage)
                .setPositiveButton("Close", null)
                .show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = displayedList[position]
            itemCount++
            val priceValue = selectedItem.price.replace(Regex("[^0-9]"), "").toInt()
            totalPrice += priceValue

            tvCartSummary.text = "Basket: $itemCount items (KES $totalPrice)"
            Toast.makeText(this, "Added ${selectedItem.name} to basket!", Toast.LENGTH_SHORT).show()
        }

        btnClear.setOnClickListener {
            itemCount = 0
            totalPrice = 0
            tvCartSummary.text = "Basket: 0 items (KES 0)"
            Toast.makeText(this, "Basket cleared", Toast.LENGTH_SHORT).show()
        }

        btnCheckout.setOnClickListener {
            if (itemCount == 0) {
                Toast.makeText(this, "Your basket is empty! Tap items to add.", Toast.LENGTH_SHORT).show()
            } else {
                showPaymentDialog()
            }
        }
    }

    private fun showPaymentDialog() {
        val paymentOptions = arrayOf(
            "📱 M-Pesa (Lipa na M-Pesa STK Push)",
            "💳 Credit / Debit Card",
            "💵 Cash on Delivery"
        )

        AlertDialog.Builder(this)
            .setTitle("Select Payment Method (Total: KES $totalPrice)")
            .setItems(paymentOptions) { _, which ->
                when (which) {
                    0 -> showMPesaPrompt()
                    1 -> processSuccessfulOrder("Credit Card")
                    2 -> processSuccessfulOrder("Cash on Delivery")
                }
            }
            .show()
    }

    private fun showMPesaPrompt() {
        val input = EditText(this)
        input.hint = "Enter M-Pesa Phone (e.g. 0712345678)"
        input.inputType = android.text.InputType.TYPE_CLASS_PHONE

        AlertDialog.Builder(this)
            .setTitle("M-Pesa Express Checkout")
            .setMessage("Enter your Safaricom phone number to receive the STK push request for KES $totalPrice:")
            .setView(input)
            .setPositiveButton("Pay Now") { _, _ ->
                val phone = input.text.toString()
                if (phone.isNotBlank()) {
                    Toast.makeText(this, "STK Push sent to $phone. Enter PIN to complete.", Toast.LENGTH_LONG).show()
                    processSuccessfulOrder("M-Pesa ($phone)")
                } else {
                    Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun processSuccessfulOrder(paymentMethod: String) {
        totalOrdersPlaced++
        val orderSummary = """
            ✅ ORDER PLACED SUCCESSFULLY!
            
            • Paid via: $paymentMethod
            • Items in Order: $itemCount
            • Total Paid: KES $totalPrice
            • Delivery Status: Dispatched 🚚
            
            Karibu tena! Thank you for shopping with Mama Mboga.
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Order Receipt")
            .setMessage(orderSummary)
            .setPositiveButton("Done") { _, _ ->
                itemCount = 0
                totalPrice = 0
                val tvCartSummary = findViewById<TextView>(R.id.textView4)
                tvCartSummary.text = "Basket: 0 items (KES 0)"
            }
            .show()
    }
}
