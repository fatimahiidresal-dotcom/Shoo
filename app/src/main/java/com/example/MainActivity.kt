package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.BazaarRepository
import com.example.ui.BazaarViewModel
import com.example.ui.BazaarViewModelFactory
import com.example.ui.MainTab
import com.example.ui.components.AddAddressDialog
import com.example.ui.components.AddCustomProductDialog
import com.example.ui.screens.CartScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersAndProfileScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val repository = remember {
                    val db = AppDatabase.getDatabase(context)
                    BazaarRepository(db.bazaarDao())
                }
                val viewModel: BazaarViewModel = viewModel(
                    factory = BazaarViewModelFactory(repository)
                )
                BazaarShoppingApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BazaarShoppingApp(
    viewModel: BazaarViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val selectedProductId by viewModel.selectedProductId.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val favoriteProducts by viewModel.favoriteProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val cartPricing by viewModel.cartPricing.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val addresses by viewModel.addresses.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsStateWithLifecycle()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddAddressDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        val msg = snackbarMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearSnackbarMessage()
        }
    }

    if (showAddProductDialog) {
        AddCustomProductDialog(
            onDismiss = { showAddProductDialog = false },
            onConfirm = { title, brand, subtitle, desc, category, price, origPrice, imageKey, isFlash ->
                showAddProductDialog = false
                viewModel.addCustomProduct(
                    title = title,
                    brand = brand,
                    subtitle = subtitle,
                    description = desc,
                    category = category,
                    price = price,
                    originalPrice = origPrice,
                    imageKey = imageKey,
                    isFlashSale = isFlash
                )
            }
        )
    }

    if (showAddAddressDialog) {
        AddAddressDialog(
            onDismiss = { showAddAddressDialog = false },
            onConfirm = { label, recipient, city, street, phone, isDefault ->
                showAddAddressDialog = false
                viewModel.addAddress(
                    label = label,
                    recipientName = recipient,
                    city = city,
                    districtAndStreet = street,
                    phone = phone,
                    isDefault = isDefault
                )
            }
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 700.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!isExpandedScreen && selectedProductId == null) {
                    BazaarBottomNavigationBar(
                        currentTab = currentTab,
                        cartBadgeCount = cartPricing.itemCount,
                        favoritesBadgeCount = favoriteProducts.size,
                        onSelectTab = viewModel::selectTab
                    )
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen) {
                    BazaarSideNavigationRail(
                        currentTab = currentTab,
                        cartBadgeCount = cartPricing.itemCount,
                        favoritesBadgeCount = favoriteProducts.size,
                        onSelectTab = viewModel::selectTab
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    val activeProductId = selectedProductId
                    if (activeProductId != null) {
                        val productFlow = remember(activeProductId) {
                            viewModel.getProductFlow(activeProductId)
                        }
                        val reviewsFlow = remember(activeProductId) {
                            viewModel.getReviewsFlow(activeProductId)
                        }
                        val activeProduct by productFlow.collectAsStateWithLifecycle(initialValue = null)
                        val activeReviews by reviewsFlow.collectAsStateWithLifecycle(initialValue = emptyList())

                        val resolvedProduct = activeProduct ?: allProducts.find { it.id == activeProductId }
                        if (resolvedProduct != null) {
                            ProductDetailScreen(
                                product = resolvedProduct,
                                reviews = activeReviews,
                                onBack = viewModel::closeProductDetail,
                                onToggleFavorite = { viewModel.toggleFavorite(resolvedProduct) },
                                onAddToCart = { qty, size, color ->
                                    viewModel.addToCart(
                                        product = resolvedProduct,
                                        quantity = qty,
                                        selectedSize = size,
                                        selectedColor = color,
                                        openCartAfter = false
                                    )
                                },
                                onBuyNow = { qty, size, color ->
                                    viewModel.addToCart(
                                        product = resolvedProduct,
                                        quantity = qty,
                                        selectedSize = size,
                                        selectedColor = color,
                                        openCartAfter = true
                                    )
                                },
                                onSubmitReview = { author, rating, comment ->
                                    viewModel.addReview(resolvedProduct, author, rating, comment)
                                }
                            )
                        }
                    } else {
                        Crossfade(targetState = currentTab, label = "main_tab_crossfade") { tab ->
                            when (tab) {
                                MainTab.HOME -> {
                                    HomeScreen(
                                        allProducts = allProducts,
                                        filteredProducts = filteredProducts,
                                        filterState = filterState,
                                        defaultAddress = addresses.firstOrNull { it.isDefault }
                                            ?: addresses.firstOrNull(),
                                        onSearchQueryChange = viewModel::updateSearchQuery,
                                        onCategorySelect = viewModel::selectCategory,
                                        onSortOptionSelect = viewModel::selectSortOption,
                                        onToggleFlashSaleFilter = viewModel::toggleFlashSaleFilter,
                                        onResetFilters = viewModel::resetFilters,
                                        onProductClick = viewModel::openProductDetail,
                                        onFavoriteClick = viewModel::toggleFavorite,
                                        onAddToCartClick = { product ->
                                            viewModel.addToCart(product, quantity = 1)
                                        },
                                        onApplyPromoCode = viewModel::applyCoupon,
                                        onOpenAddProductDialog = { showAddProductDialog = true }
                                    )
                                }

                                MainTab.FAVORITES -> {
                                    FavoritesScreen(
                                        favoriteProducts = favoriteProducts,
                                        onBackToHome = { viewModel.selectTab(MainTab.HOME) },
                                        onProductClick = viewModel::openProductDetail,
                                        onToggleFavorite = viewModel::toggleFavorite,
                                        onAddToCart = { product ->
                                            viewModel.addToCart(product, quantity = 1)
                                        },
                                        onMoveAllToCart = {
                                            viewModel.moveAllFavoritesToCart(favoriteProducts)
                                        }
                                    )
                                }

                                MainTab.CART -> {
                                    CartScreen(
                                        cartItems = cartItems,
                                        pricing = cartPricing,
                                        addresses = addresses,
                                        lastPlacedOrder = lastPlacedOrder,
                                        onBackToHome = { viewModel.selectTab(MainTab.HOME) },
                                        onProductClick = viewModel::openProductDetail,
                                        onUpdateQuantity = viewModel::updateCartItemQuantity,
                                        onRemoveItem = viewModel::removeCartItem,
                                        onApplyCoupon = viewModel::applyCoupon,
                                        onRemoveCoupon = viewModel::removeCoupon,
                                        onOpenAddAddressDialog = { showAddAddressDialog = true },
                                        onSubmitOrder = viewModel::submitOrder,
                                        onDismissOrderSuccess = viewModel::dismissOrderConfirmationAndViewOrders
                                    )
                                }

                                MainTab.ORDERS -> {
                                    OrdersAndProfileScreen(
                                        orders = orders,
                                        addresses = addresses,
                                        onBackToHome = { viewModel.selectTab(MainTab.HOME) },
                                        onAdvanceOrderStatus = viewModel::advanceOrderStatus,
                                        onOpenAddAddressDialog = { showAddAddressDialog = true },
                                        onSelectDefaultAddress = viewModel::selectDefaultAddress,
                                        onDeleteAddress = viewModel::deleteAddress
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BazaarBottomNavigationBar(
    currentTab: MainTab,
    cartBadgeCount: Int,
    favoritesBadgeCount: Int,
    onSelectTab: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        MainTab.entries.forEach { tab ->
            val selected = currentTab == tab
            val badgeCount = when (tab) {
                MainTab.CART -> cartBadgeCount
                MainTab.FAVORITES -> favoritesBadgeCount
                else -> 0
            }
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge(
                                    containerColor = if (tab == MainTab.CART) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.secondary
                                    }
                                ) {
                                    Text("$badgeCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = tab.icon(selected),
                            contentDescription = tab.titleAr
                        )
                    }
                },
                label = {
                    Text(
                        text = tab.titleAr,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                modifier = Modifier.testTag(tab.routeTag)
            )
        }
    }
}

@Composable
private fun BazaarSideNavigationRail(
    currentTab: MainTab,
    cartBadgeCount: Int,
    favoritesBadgeCount: Int,
    onSelectTab: (MainTab) -> Unit
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        MainTab.entries.forEach { tab ->
            val selected = currentTab == tab
            val badgeCount = when (tab) {
                MainTab.CART -> cartBadgeCount
                MainTab.FAVORITES -> favoritesBadgeCount
                else -> 0
            }
            NavigationRailItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge {
                                    Text("$badgeCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = tab.icon(selected),
                            contentDescription = tab.titleAr
                        )
                    }
                },
                label = { Text(tab.titleAr) },
                modifier = Modifier.testTag(tab.routeTag)
            )
        }
    }
}

private fun MainTab.icon(selected: Boolean): ImageVector {
    return when (this) {
        MainTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
        MainTab.FAVORITES -> if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
        MainTab.CART -> if (selected) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart
        MainTab.ORDERS -> if (selected) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong
    }
}
