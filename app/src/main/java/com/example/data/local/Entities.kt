package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val brand: String,
    val subtitle: String,
    val description: String,
    val category: String,
    val price: Double,
    val originalPrice: Double? = null,
    val rating: Double = 4.8,
    val reviewCount: Int = 24,
    val imageKey: String, // "electronics", "fashion", "perfume", "hero"
    val badgeText: String? = null,
    val isFavorite: Boolean = false,
    val isFlashSale: Boolean = false,
    val stockCount: Int = 15,
    val availableSizes: String = "قياسي", // comma-separated
    val availableColors: String = "ذهبي,أسود", // comma-separated
    val specs: String = "ضمان أصالة 100%|شحن سريع خلال 24 ساعة|استبدال واسترجاع مجاني خلال 14 يوماً"
) {
    val discountPercentage: Int
        get() {
            val orig = originalPrice ?: return 0
            if (orig <= price || orig <= 0.0) return 0
            return (((orig - price) / orig) * 100).toInt()
        }
}

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val productTitle: String,
    val productBrand: String,
    val price: Double,
    val originalPrice: Double?,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String,
    val imageKey: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderNumber: String,
    val createdAt: Long = System.currentTimeMillis(),
    val itemsSummary: String,
    val itemCount: Int,
    val subtotal: Double,
    val discountAmount: Double,
    val shippingFee: Double,
    val totalAmount: Double,
    val couponCode: String?,
    val recipientName: String,
    val city: String,
    val shippingAddress: String,
    val phoneNumber: String,
    val paymentMethod: String,
    val statusStep: Int = 1 // 1 = قيد التجهيز, 2 = في الطريق إليك, 3 = تم التسليم
) {
    val statusLabel: String
        get() = when (statusStep) {
            1 -> "قيد التجهيز"
            2 -> "في الطريق إليك"
            else -> "تم التسليم"
        }
}

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val authorName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val label: String,
    val recipientName: String,
    val city: String,
    val districtAndStreet: String,
    val phone: String,
    val isDefault: Boolean = false
)
