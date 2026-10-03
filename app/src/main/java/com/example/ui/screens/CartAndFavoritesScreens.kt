package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.AddressEntity
import com.example.data.local.CartItemEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.ui.CartPricingSummary
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.components.formatPriceSar
import com.example.ui.components.resolveProductImageRes
import com.example.ui.theme.SaleRed
import com.example.ui.theme.SuccessGreen

@Composable
fun FavoritesScreen(
    favoriteProducts: List<ProductEntity>,
    onBackToHome: () -> Unit,
    onProductClick: (Int) -> Unit,
    onToggleFavorite: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onMoveAllToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToHome)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("favorites_screen")
    ) {
        val columnsCount = when {
            maxWidth >= 840.dp -> 4
            maxWidth >= 600.dp -> 3
            else -> 2
        }
        val chunkedFavorites = remember(favoriteProducts, columnsCount) {
            favoriteProducts.chunked(columnsCount)
        }

        if (favoriteProducts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Filled.FavoriteBorder,
                    title = "قائمة المفضلة فارغة",
                    subtitle = "اضغط على رمز القلب على أي منتج لحفظه هنا والرجوع إليه في أي وقت",
                    actionLabel = "استكشف المنتجات الآن",
                    onActionClick = onBackToHome
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "منتجاتي المفضلة (${favoriteProducts.size})",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "احتفظنا باختياراتك المفضلة لسهولة الوصول",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = onMoveAllToCart,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("move_all_favorites_to_cart_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("نقل الكل للسلة")
                        }
                    }
                }

                items(chunkedFavorites) { rowProducts ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductCard(
                                product = product,
                                onProductClick = { onProductClick(product.id) },
                                onFavoriteClick = { onToggleFavorite(product) },
                                onAddToCartClick = { onAddToCart(product) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(columnsCount - rowProducts.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CartScreen(
    cartItems: List<CartItemEntity>,
    pricing: CartPricingSummary,
    addresses: List<AddressEntity>,
    lastPlacedOrder: OrderEntity?,
    onBackToHome: () -> Unit,
    onProductClick: (Int) -> Unit,
    onUpdateQuantity: (CartItemEntity, Int) -> Unit,
    onRemoveItem: (CartItemEntity) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit,
    onOpenAddAddressDialog: () -> Unit,
    onSubmitOrder: (AddressEntity, String) -> Unit,
    onDismissOrderSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackToHome)

    var couponInput by remember { mutableStateOf("") }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    if (lastPlacedOrder != null) {
        OrderSuccessDialog(
            order = lastPlacedOrder,
            onViewOrders = onDismissOrderSuccess
        )
    }

    if (showCheckoutDialog && cartItems.isNotEmpty()) {
        CheckoutModalDialog(
            addresses = addresses,
            pricing = pricing,
            onDismiss = { showCheckoutDialog = false },
            onAddNewAddress = onOpenAddAddressDialog,
            onConfirmOrder = { address, paymentMethod ->
                showCheckoutDialog = false
                onSubmitOrder(address, paymentMethod)
            }
        )
    }

    if (cartItems.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .testTag("cart_screen_empty"),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                icon = Icons.Filled.ShoppingCart,
                title = "سلة المشتريات فارغة",
                subtitle = "تصفح تشكيلة بازار الفاخرة من العطور والإلكترونيات والأزياء وأضف ما يعجبك",
                actionLabel = "ابدأ التسوق الآن",
                onActionClick = onBackToHome
            )
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .testTag("cart_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "سلة المشتريات (${pricing.itemCount} منتجات)",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Free Shipping Progress Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (pricing.shippingFee == 0.0) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.secondaryContainer
                        }
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocalShipping,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (pricing.shippingFee == 0.0) {
                                    "تهانينا! لقد حصلت على شحن مجاني سريع لطلبك ✓"
                                } else {
                                    "أضف ${formatPriceSar(pricing.amountNeededForFreeShipping)} إضافية للحصول على شحن مجاني!"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val shipProgress = if (pricing.shippingFee == 0.0) {
                            1f
                        } else {
                            ((pricing.subtotal - pricing.couponDiscount) / pricing.freeShippingThreshold)
                                .toFloat()
                                .coerceIn(0.1f, 0.98f)
                        }
                        LinearProgressIndicator(
                            progress = { shipProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(50)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Cart Items List
            items(cartItems, key = { "cart_item_${it.id}" }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cart_item_card_${item.id}")
                        .clickable { onProductClick(item.productId) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = resolveProductImageRes(item.imageKey)),
                            contentDescription = item.productTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(86.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.productBrand,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = item.productTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(
                                    onClick = { onRemoveItem(item) },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("remove_cart_item_${item.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DeleteOutline,
                                        contentDescription = "حذف من السلة",
                                        tint = SaleRed
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "الخيار: ${item.selectedSize} • اللون: ${item.selectedColor}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = formatPriceSar(item.price * item.quantity),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = { onUpdateQuantity(item, item.quantity - 1) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("decrease_qty_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Remove,
                                                contentDescription = "إنقاص",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Text(
                                            text = "${item.quantity}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(
                                            onClick = { onUpdateQuantity(item, item.quantity + 1) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("increase_qty_${item.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = "زيادة",
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Promo Coupon Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocalOffer,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "كود الخصم والعروض الترويجية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (pricing.couponCode != null) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "تم تفعيل الكود: ${pricing.couponCode}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    IconButton(onClick = onRemoveCoupon) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "إلغاء الكوبون"
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("أدخل الكود (مثال: BAZAAR20)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("coupon_code_input")
                                )
                                Button(
                                    onClick = { onApplyCoupon(couponInput) },
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("apply_coupon_button")
                                ) {
                                    Text("تطبيق")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "أكواد متاحة للضغط السريع:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AssistChip(
                                    onClick = { onApplyCoupon("BAZAAR20") },
                                    label = { Text("BAZAAR20 (خصم 20%)") },
                                    modifier = Modifier.testTag("quick_coupon_bazaar20")
                                )
                                AssistChip(
                                    onClick = { onApplyCoupon("WELCOME50") },
                                    label = { Text("WELCOME50 (-50 ر.س)") },
                                    modifier = Modifier.testTag("quick_coupon_welcome50")
                                )
                                AssistChip(
                                    onClick = { onApplyCoupon("FREE") },
                                    label = { Text("FREE (شحن مجاني)") }
                                )
                            }
                        }
                    }
                }
            }

            // Order Summary Breakdown & Checkout Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ملخص الفاتورة",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("المجموع الفرعي (${pricing.itemCount} قطع)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatPriceSar(pricing.subtotal), fontWeight = FontWeight.SemiBold)
                        }

                        if (pricing.productSavings > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("توفير العروض على المنتجات", color = SuccessGreen)
                                Text("- ${formatPriceSar(pricing.productSavings)}", color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (pricing.couponDiscount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("خصم الكوبون (${pricing.couponCode})", color = SuccessGreen)
                                Text("- ${formatPriceSar(pricing.couponDiscount)}", color = SuccessGreen, fontWeight = FontWeight.Bold)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("رسوم التوصيل السريع", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (pricing.shippingFee == 0.0) "مجاني ✓" else formatPriceSar(pricing.shippingFee),
                                color = if (pricing.shippingFee == 0.0) SuccessGreen else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "الإجمالي النهائي",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "شامل ضريبة القيمة المضافة 15%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = formatPriceSar(pricing.total),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { showCheckoutDialog = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("proceed_to_checkout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCartCheckout,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "إتمام الشراء والدفع (${formatPriceSar(pricing.total)})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CheckoutModalDialog(
    addresses: List<AddressEntity>,
    pricing: CartPricingSummary,
    onDismiss: () -> Unit,
    onAddNewAddress: () -> Unit,
    onConfirmOrder: (AddressEntity, String) -> Unit
) {
    val fallbackAddress = AddressEntity(
        id = 0,
        label = "المنزل - الرياض",
        recipientName = "سلطان العتيبي",
        city = "الرياض",
        districtAndStreet = "حي الملقا، طريق أنس بن مالك",
        phone = "0501234567",
        isDefault = true
    )
    var selectedAddressId by remember(addresses) {
        mutableStateOf(addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id ?: 0)
    }
    val paymentMethods = listOf(
        "بطاقة مدى / ائتمانية",
        "الدفع عند الاستلام",
        "تقسيط 4 دفعات بدون فوائد"
    )
    var selectedPayment by remember { mutableStateOf(paymentMethods.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تأكيد بيانات التوصيل والدفع",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "عنوان التوصيل:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = onAddNewAddress) {
                        Icon(
                            imageVector = Icons.Filled.AddLocationAlt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("عنوان جديد")
                    }
                }

                val displayAddresses = addresses.ifEmpty { listOf(fallbackAddress) }
                displayAddresses.forEach { addr ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedAddressId = addr.id },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedAddressId == addr.id) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            }
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedAddressId == addr.id,
                                onClick = { selectedAddressId = addr.id }
                            )
                            Column {
                                Text(
                                    text = "${addr.label} (${addr.recipientName})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${addr.city} - ${addr.districtAndStreet}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "جوال: ${addr.phone}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "وسيلة الدفع المفضلة:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    paymentMethods.forEach { method ->
                        FilterChip(
                            selected = selectedPayment == method,
                            onClick = { selectedPayment = method },
                            label = { Text(method) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (method.contains("بطاقة")) {
                                        Icons.Filled.CreditCard
                                    } else {
                                        Icons.Filled.Payments
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("المبلغ المستحق للدفع:", fontWeight = FontWeight.Bold)
                    Text(
                        text = formatPriceSar(pricing.total),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val chosen = addresses.find { it.id == selectedAddressId }
                        ?: addresses.firstOrNull()
                        ?: fallbackAddress
                    onConfirmOrder(chosen, selectedPayment)
                },
                modifier = Modifier.testTag("confirm_order_submit_button")
            ) {
                Text("تأكيد وإرسال الطلب")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("رجوع")
            }
        }
    )
}

@Composable
private fun OrderSuccessDialog(
    order: OrderEntity,
    onViewOrders: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onViewOrders,
        icon = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
        },
        title = {
            Text(
                text = "تم استلام طلبك بنجاح!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "رقم الطلب: ${order.orderNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "الإجمالي: ${formatPriceSar(order.totalAmount)} (${order.paymentMethod})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "سيتم توصيل شحنتك إلى: ${order.city} - ${order.shippingAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onViewOrders,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("view_order_tracking_button")
            ) {
                Text("تتبع حالة الشحنة الآن")
            }
        }
    )
}
