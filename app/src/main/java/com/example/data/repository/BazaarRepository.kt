package com.example.data.repository

import com.example.data.local.AddressEntity
import com.example.data.local.BazaarDao
import com.example.data.local.CartItemEntity
import com.example.data.local.InitialCatalogSeeder
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReviewEntity
import kotlinx.coroutines.flow.Flow

class BazaarRepository(private val dao: BazaarDao) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val favoriteProducts: Flow<List<ProductEntity>> = dao.getFavoriteProducts()
    val cartItems: Flow<List<CartItemEntity>> = dao.getAllCartItems()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allAddresses: Flow<List<AddressEntity>> = dao.getAllAddresses()

    fun getProductById(productId: Int): Flow<ProductEntity?> = dao.getProductByIdFlow(productId)

    fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>> =
        dao.getReviewsForProduct(productId)

    suspend fun ensureSeeded() {
        if (dao.getProductCount() == 0) {
            dao.insertProducts(InitialCatalogSeeder.getInitialProducts())
            dao.insertReviews(InitialCatalogSeeder.getInitialReviews())
        }
        if (dao.getAddressCount() == 0) {
            dao.insertAddresses(InitialCatalogSeeder.getInitialAddresses())
        }
    }

    suspend fun toggleFavorite(product: ProductEntity) {
        dao.updateFavoriteStatus(product.id, !product.isFavorite)
    }

    suspend fun addToCart(
        product: ProductEntity,
        quantity: Int = 1,
        selectedSize: String? = null,
        selectedColor: String? = null
    ) {
        val size = selectedSize ?: product.availableSizes.split(",").firstOrNull()?.trim() ?: "قياسي"
        val color = selectedColor ?: product.availableColors.split(",").firstOrNull()?.trim() ?: "قياسي"
        val existing = dao.findCartItem(product.id, size, color)
        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            dao.insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    productTitle = product.title,
                    productBrand = product.brand,
                    price = product.price,
                    originalPrice = product.originalPrice,
                    quantity = quantity,
                    selectedSize = size,
                    selectedColor = color,
                    imageKey = product.imageKey
                )
            )
        }
    }

    suspend fun updateCartQuantity(item: CartItemEntity, newQuantity: Int) {
        if (newQuantity <= 0) {
            dao.deleteCartItemById(item.id)
        } else {
            dao.updateCartItem(item.copy(quantity = newQuantity))
        }
    }

    suspend fun removeCartItem(cartItemId: Int) {
        dao.deleteCartItemById(cartItemId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun placeOrder(order: OrderEntity) {
        dao.insertOrder(order)
        dao.clearCart()
    }

    suspend fun advanceOrderStatus(order: OrderEntity) {
        val next = if (order.statusStep < 3) order.statusStep + 1 else 1
        dao.updateOrderStatusStep(order.id, next)
    }

    suspend fun addReview(
        product: ProductEntity,
        authorName: String,
        rating: Int,
        comment: String
    ) {
        dao.insertReview(
            ReviewEntity(
                productId = product.id,
                authorName = authorName.ifBlank { "عميل بازار" },
                rating = rating,
                comment = comment
            )
        )
        val newCount = product.reviewCount + 1
        val weightedRating = ((product.rating * product.reviewCount) + rating) / newCount
        val rounded = kotlin.math.round(weightedRating * 10.0) / 10.0
        dao.updateProductRating(product.id, rounded, newCount)
    }

    suspend fun addCustomProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    suspend fun addAddress(address: AddressEntity) {
        if (address.isDefault) {
            dao.clearDefaultAddresses()
        }
        dao.insertAddress(address)
    }

    suspend fun selectDefaultAddress(addressId: Int) {
        dao.clearDefaultAddresses()
        dao.setDefaultAddress(addressId)
    }

    suspend fun deleteAddress(addressId: Int) {
        dao.deleteAddressById(addressId)
    }
}
