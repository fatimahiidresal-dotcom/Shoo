package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AddressEntity
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReviewEntity
import com.example.data.repository.BazaarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class MainTab(val titleAr: String, val routeTag: String) {
    HOME("الرئيسية", "nav_tab_home"),
    FAVORITES("المفضلة", "nav_tab_favorites"),
    CART("السلة", "nav_tab_cart"),
    ORDERS("طلباتي", "nav_tab_orders")
}

enum class SortOption(val labelAr: String) {
    POPULAR("الأكثر شعبية"),
    PRICE_LOW_TO_HIGH("السعر: من الأقل للأعلى"),
    PRICE_HIGH_TO_LOW("السعر: من الأعلى للأقل"),
    HIGHEST_RATED("الأعلى تقييماً")
}

data class FilterState(
    val searchQuery: String = "",
    val selectedCategory: String = "الكل",
    val sortOption: SortOption = SortOption.POPULAR,
    val onlyFlashSale: Boolean = false
)

data class CartPricingSummary(
    val itemCount: Int = 0,
    val subtotal: Double = 0.0,
    val productSavings: Double = 0.0,
    val couponCode: String? = null,
    val couponDiscount: Double = 0.0,
    val shippingFee: Double = 0.0,
    val freeShippingThreshold: Double = 400.0,
    val amountNeededForFreeShipping: Double = 400.0,
    val total: Double = 0.0
)

class BazaarViewModel(private val repository: BazaarRepository) : ViewModel() {

    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _selectedProductId = MutableStateFlow<Int?>(null)
    val selectedProductId: StateFlow<Int?> = _selectedProductId.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        repository.allProducts,
        _filterState
    ) { products, filter ->
        products.filter { product ->
            val matchesCategory = filter.selectedCategory == "الكل" ||
                product.category == filter.selectedCategory
            val q = filter.searchQuery.trim()
            val matchesQuery = q.isEmpty() ||
                product.title.contains(q, ignoreCase = true) ||
                product.brand.contains(q, ignoreCase = true) ||
                product.subtitle.contains(q, ignoreCase = true) ||
                product.category.contains(q, ignoreCase = true)
            val matchesFlash = !filter.onlyFlashSale || product.isFlashSale
            matchesCategory && matchesQuery && matchesFlash
        }.let { list ->
            when (filter.sortOption) {
                SortOption.POPULAR -> list.sortedWith(
                    compareByDescending<ProductEntity> { it.isFlashSale }
                        .thenByDescending { it.reviewCount }
                )
                SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.price }
                SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.price }
                SortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteProducts: StateFlow<List<ProductEntity>> = repository.favoriteProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val addresses: StateFlow<List<AddressEntity>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartPricing: StateFlow<CartPricingSummary> = combine(
        repository.cartItems,
        _appliedCoupon
    ) { items, coupon ->
        if (items.isEmpty()) {
            CartPricingSummary()
        } else {
            val totalCount = items.sumOf { it.quantity }
            val subtotal = items.sumOf { it.price * it.quantity }
            val productSavings = items.sumOf { item ->
                val orig = item.originalPrice ?: item.price
                ((orig - item.price).coerceAtLeast(0.0)) * item.quantity
            }
            val couponDiscount = when (coupon?.uppercase()) {
                "BAZAAR20" -> kotlin.math.round(subtotal * 0.20)
                "WELCOME50" -> if (subtotal >= 150.0) 50.0 else kotlin.math.round(subtotal * 0.15)
                else -> 0.0
            }
            val afterDiscount = (subtotal - couponDiscount).coerceAtLeast(0.0)
            val threshold = 400.0
            val isFreeShipping = afterDiscount >= threshold || coupon?.uppercase() == "FREE"
            val shippingFee = if (isFreeShipping) 0.0 else 25.0
            val neededForFree = (threshold - afterDiscount).coerceAtLeast(0.0)
            val finalTotal = afterDiscount + shippingFee

            CartPricingSummary(
                itemCount = totalCount,
                subtotal = subtotal,
                productSavings = productSavings,
                couponCode = coupon,
                couponDiscount = couponDiscount,
                shippingFee = shippingFee,
                freeShippingThreshold = threshold,
                amountNeededForFreeShipping = neededForFree,
                total = finalTotal
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartPricingSummary())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    fun selectTab(tab: MainTab) {
        _selectedProductId.value = null
        _currentTab.value = tab
    }

    fun openProductDetail(productId: Int) {
        _selectedProductId.value = productId
    }

    fun closeProductDetail() {
        _selectedProductId.value = null
    }

    fun getProductFlow(productId: Int): Flow<ProductEntity?> = repository.getProductById(productId)

    fun getReviewsFlow(productId: Int): Flow<List<ReviewEntity>> =
        repository.getReviewsForProduct(productId)

    fun updateSearchQuery(query: String) {
        _filterState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: String) {
        _filterState.update { it.copy(selectedCategory = category) }
    }

    fun selectSortOption(option: SortOption) {
        _filterState.update { it.copy(sortOption = option) }
    }

    fun toggleFlashSaleFilter() {
        _filterState.update { it.copy(onlyFlashSale = !it.onlyFlashSale) }
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    fun toggleFavorite(product: ProductEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(product)
            val msg = if (!product.isFavorite) {
                "تمت إضافة \"${product.title}\" إلى المفضلة"
            } else {
                "تمت إزالة المنتج من المفضلة"
            }
            _snackbarMessage.value = msg
        }
    }

    fun addToCart(
        product: ProductEntity,
        quantity: Int = 1,
        selectedSize: String? = null,
        selectedColor: String? = null,
        openCartAfter: Boolean = false
    ) {
        viewModelScope.launch {
            repository.addToCart(product, quantity, selectedSize, selectedColor)
            if (openCartAfter) {
                _selectedProductId.value = null
                _currentTab.value = MainTab.CART
            } else {
                _snackbarMessage.value = "تمت إضافة \"${product.title}\" إلى السلة ✓"
            }
        }
    }

    fun moveAllFavoritesToCart(favorites: List<ProductEntity>) {
        if (favorites.isEmpty()) return
        viewModelScope.launch {
            favorites.forEach { product ->
                repository.addToCart(product, quantity = 1)
            }
            _snackbarMessage.value = "تم نقل ${favorites.size} منتجات إلى سلة المشتريات ✓"
            _currentTab.value = MainTab.CART
        }
    }

    fun updateCartItemQuantity(item: CartItemEntity, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(item, newQuantity)
        }
    }

    fun removeCartItem(item: CartItemEntity) {
        viewModelScope.launch {
            repository.removeCartItem(item.id)
            _snackbarMessage.value = "تم حذف \"${item.productTitle}\" من السلة"
        }
    }

    fun applyCoupon(code: String) {
        val clean = code.trim().uppercase()
        when (clean) {
            "BAZAAR20" -> {
                _appliedCoupon.value = "BAZAAR20"
                _snackbarMessage.value = "تم تطبيق كود الخصم BAZAAR20 (خصم 20%) بنجاح!"
            }
            "WELCOME50" -> {
                _appliedCoupon.value = "WELCOME50"
                _snackbarMessage.value = "تم تطبيق كود الترحيب WELCOME50 بنجاح!"
            }
            "FREE" -> {
                _appliedCoupon.value = "FREE"
                _snackbarMessage.value = "تم تفعيل الشحن المجاني عبر الكود FREE!"
            }
            else -> {
                _snackbarMessage.value = "كود الخصم غير صالح. جرب BAZAAR20 أو WELCOME50 أو FREE"
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _snackbarMessage.value = "تم إلغاء كود الخصم"
    }

    fun submitOrder(
        address: AddressEntity,
        paymentMethod: String
    ) {
        val items = cartItems.value
        val pricing = cartPricing.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val orderNum = "BZ-${Random.nextInt(100000, 999999)}"
            val summary = items.joinToString(" • ") {
                "${it.productTitle} (×${it.quantity} - ${it.selectedSize})"
            }
            val newOrder = OrderEntity(
                orderNumber = orderNum,
                itemsSummary = summary,
                itemCount = pricing.itemCount,
                subtotal = pricing.subtotal,
                discountAmount = pricing.couponDiscount,
                shippingFee = pricing.shippingFee,
                totalAmount = pricing.total,
                couponCode = pricing.couponCode,
                recipientName = address.recipientName,
                city = address.city,
                shippingAddress = "${address.label} - ${address.districtAndStreet}",
                phoneNumber = address.phone,
                paymentMethod = paymentMethod,
                statusStep = 1
            )
            repository.placeOrder(newOrder)
            _appliedCoupon.value = null
            _lastPlacedOrder.value = newOrder
        }
    }

    fun dismissOrderConfirmationAndViewOrders() {
        _lastPlacedOrder.value = null
        _selectedProductId.value = null
        _currentTab.value = MainTab.ORDERS
    }

    fun advanceOrderStatus(order: OrderEntity) {
        viewModelScope.launch {
            repository.advanceOrderStatus(order)
            _snackbarMessage.value = "تم تحديث حالة الشحنة للطلب ${order.orderNumber}"
        }
    }

    fun addReview(product: ProductEntity, authorName: String, rating: Int, comment: String) {
        if (comment.isBlank()) return
        viewModelScope.launch {
            repository.addReview(product, authorName, rating, comment)
            _snackbarMessage.value = "شكراً لك! تم نشر تقييمك بنجاح"
        }
    }

    fun addCustomProduct(
        title: String,
        brand: String,
        subtitle: String,
        description: String,
        category: String,
        price: Double,
        originalPrice: Double?,
        imageKey: String,
        isFlashSale: Boolean
    ) {
        viewModelScope.launch {
            val product = ProductEntity(
                title = title,
                brand = brand.ifBlank { "بازار سيلكت" },
                subtitle = subtitle.ifBlank { "منتج مختار بعناية بجودة عالية" },
                description = description.ifBlank { subtitle },
                category = category,
                price = price,
                originalPrice = originalPrice,
                rating = 5.0,
                reviewCount = 1,
                imageKey = imageKey,
                badgeText = if (isFlashSale) "عرض خاص" else "جديد",
                isFavorite = false,
                isFlashSale = isFlashSale
            )
            repository.addCustomProduct(product)
            _snackbarMessage.value = "تمت إضافة المنتج \"$title\" إلى المتجر بنجاح ✓"
        }
    }

    fun addAddress(
        label: String,
        recipientName: String,
        city: String,
        districtAndStreet: String,
        phone: String,
        isDefault: Boolean
    ) {
        viewModelScope.launch {
            repository.addAddress(
                AddressEntity(
                    label = label.ifBlank { "عنوان التوصيل" },
                    recipientName = recipientName.ifBlank { "عميل بازار" },
                    city = city.ifBlank { "الرياض" },
                    districtAndStreet = districtAndStreet,
                    phone = phone.ifBlank { "0500000000" },
                    isDefault = isDefault
                )
            )
            _snackbarMessage.value = "تم حفظ عنوان التوصيل بنجاح"
        }
    }

    fun selectDefaultAddress(addressId: Int) {
        viewModelScope.launch {
            repository.selectDefaultAddress(addressId)
        }
    }

    fun deleteAddress(addressId: Int) {
        viewModelScope.launch {
            repository.deleteAddress(addressId)
            _snackbarMessage.value = "تم حذف العنوان"
        }
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

class BazaarViewModelFactory(private val repository: BazaarRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BazaarViewModel::class.java)) {
            return BazaarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
