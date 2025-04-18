package ru.kicker721.lab12

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.list)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val list = findViewById<RecyclerView>(R.id.list)
        val products = listOf(
            Product("Арахисовая паста", 179.99, R.drawable.food01),
            Product("Пицца", 499.99, R.drawable.food02),
            Product("Рис", 119.99, R.drawable.food03),
            Product("Брокколи", 339.99, R.drawable.food04),
            Product("Сыр", 899.99, R.drawable.food05),
            Product("Каша", 49.99, R.drawable.food06),
            Product("Молоко", 89.99, R.drawable.food07),
        )

        val adapter = ProductsAdapter(products)
        adapter.setOnClickListener { pos ->
            if(products[pos].isInCart){
                val sb = Snackbar.make(list, "Добавлено: ${products[pos].name}", Snackbar.LENGTH_SHORT) //products[pos].isInCart
                sb.setBackgroundTint(getColor(R.color.green))
                sb.show()
            }
            else{
                val sb = Snackbar.make(list, "Удалено: ${products[pos].name}", Snackbar.LENGTH_SHORT) //products[pos].isInCart
                sb.setBackgroundTint(getColor(R.color.red))
                sb.show()
            }

        }

        list.adapter = adapter
    }
}

class ProductsAdapter(val products: List<Product>) :
    RecyclerView.Adapter<ProductsAdapter.ProductHolder>() {

    class ProductHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvPrice: TextView
        var imageView: ImageView
        var cartBtn: ImageButton
        init{
            tvName = itemView.findViewById(R.id.product_name)
            tvPrice = itemView.findViewById(R.id.product_price)
            imageView = itemView.findViewById(R.id.product_image)
            cartBtn = itemView.findViewById(R.id.cart_btn)
        }
        fun updateCartIcon(isAdded: Boolean) {
            val iconRes = if (isAdded) R.drawable.shopping_cart else R.drawable.shopping_cart_add
            this.cartBtn.setImageResource(iconRes)
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnClickListener(f: (Int) -> Unit) {onItemClickListener = f}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_item, parent, false)
        val holder = ProductHolder(view)

        return holder
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val context = holder.itemView.context
        holder.tvName.text = products[position].name
        holder.tvPrice.text = context.getString(R.string.price, products[position].price)
        holder.imageView.setImageResource(products[position].imageId)
        holder.cartBtn.setImageResource(if(products[position].isInCart) R.drawable.shopping_cart else R.drawable.shopping_cart_add)
        holder.cartBtn.setOnClickListener {
            products[position].isInCart = !products[position].isInCart
            holder.updateCartIcon(products[position].isInCart)
            onItemClickListener?.invoke(position)
        }
    }
}