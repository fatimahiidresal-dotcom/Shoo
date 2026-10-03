package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        ReviewEntity::class,
        AddressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bazaarDao(): BazaarDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bazaar_shopping_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object InitialCatalogSeeder {
    fun getInitialProducts(): List<ProductEntity> = listOf(
        ProductEntity(
            id = 1,
            title = "عطر عود ملكي خاص - أو دو بارفيوم",
            brand = "دار الطيب الملكي",
            subtitle = "مزيج فاخر من دهن العود الكمبودي والزعفران والعنبر الدافئ",
            description = "عطر شرقي ملكي مصمم بعناية من أندر أخشاب العود المعتقة مع نفحات الزعفران الفاخر والورد الدمشقي وقاعدة غنية من العنبر والمسك الأبيض. يدوم ثباته لأكثر من 36 ساعة في زجاجة كريستالية فاخرة.",
            category = "عطور وتجميل",
            price = 385.0,
            originalPrice = 520.0,
            rating = 4.9,
            reviewCount = 148,
            imageKey = "perfume",
            badgeText = "الأكثر مبيعاً",
            isFavorite = true,
            isFlashSale = true,
            stockCount = 12,
            availableSizes = "50 مل,100 مل,150 مل (إصدار خاص)",
            availableColors = "عنبر ذهبي,أسود ملكي",
            specs = "التركيز: أو دو بارفيوم مكثف|الثبات: حتى 36 ساعة|بلد المنشأ: تحضير يدوي فاخر|تغليف هدايا مجاني"
        ),
        ProductEntity(
            id = 2,
            title = "سماعة رأس لاسلكية تيتانيوم بعزل ذكي",
            brand = "أورو تيك AuroTech",
            subtitle = "صوت محيطي فائق النقاء مع بطارية تدوم 45 ساعة",
            description = "سماعة لاسلكية احترافية مصنوعة من التيتانيوم المصقول والجلد الطبيعي المريح للأذن. تدعم تقنية العزل النشط للضوضاء (ANC) الجيل الخامس مع ميكروفونات ذكية للمكالمات الواضحة والشحن السريع.",
            category = "إلكترونيات",
            price = 749.0,
            originalPrice = 999.0,
            rating = 4.8,
            reviewCount = 96,
            imageKey = "electronics",
            badgeText = "خصم 25%",
            isFavorite = false,
            isFlashSale = true,
            stockCount = 8,
            availableSizes = "قياسي,مع حقيبة سفر صلبة",
            availableColors = "تيتانيوم رملي,أسود مطفي,ذهبي برونزي",
            specs = "البطارية: 45 ساعة متواصلة|الاتصال: بلوتوث 5.4 متعدد الأجهزة|الضمان: سنتان شامل الاستبدال"
        ),
        ProductEntity(
            id = 3,
            title = "حقيبة جلد إيطالي طبيعي فاخرة",
            brand = "ميزون لورين",
            subtitle = "تصميم عصري أنيق مشغول يدوياً مع إكسسوارات مطلية بالذهب",
            description = "حقيبة كتف ويد مصممة من الجلد الطبيعي الإيطالي الفاخر المقاوم للخدش، مزودة بجيوب داخلية منظمة للأجهزة اللوحية والمقتنيات الشخصية وحزام كتف قابل للتعديل.",
            category = "أزياء",
            price = 620.0,
            originalPrice = 850.0,
            rating = 4.9,
            reviewCount = 74,
            imageKey = "fashion",
            badgeText = "إصدار محدود",
            isFavorite = true,
            isFlashSale = true,
            stockCount = 6,
            availableSizes = "متوسط,كبير (يتسع للابتوب 14 بوصة)",
            availableColors = "جملي دافئ,زمردي داكن,عاجي",
            specs = "الخامة: جلد طبيعي 100%|البطانة: مخمل فاخر مقاوم للماء|الملحقات: كيس حفظ قماشي + بطاقة أصالة"
        ),
        ProductEntity(
            id = 4,
            title = "ساعة ذكية ألترا بإطار تيتانيوم وياقوت",
            brand = "كرونو بلس",
            subtitle = "شاشة AMOLED ساطعة ومستشعرات حيوية متكاملة",
            description = "ساعة ذكية فاخرة تجمع بين فخامة الساعات الكلاسيكية وأحدث التقنيات الصحية والرياضية. زجاج كريستال الياقوت المقاوم للخدش، مقاومة للماء حتى 50 متراً، ودعم الدفع اللاتلامسي.",
            category = "إلكترونيات",
            price = 1150.0,
            originalPrice = 1490.0,
            rating = 4.7,
            reviewCount = 112,
            imageKey = "electronics",
            badgeText = "جديد",
            isFavorite = false,
            isFlashSale = false,
            stockCount = 19,
            availableSizes = "42 ملم,46 ملم",
            availableColors = "تيتانيوم طبيعي,أسود فحمي,ذهبي صحراوي",
            specs = "الشاشة: كريستال ياقوت AMOLED|مقاومة الماء: 50 متر (5ATM)|عمر البطارية: 10 أيام"
        ),
        ProductEntity(
            id = 5,
            title = "مجموعة القهوة المختصة والإسبريسو الاحترافية",
            brand = "كافيه أرتيزان",
            subtitle = "طقم متكامل لتحضير الإسبريسو والقهوة المقطرة بمعايير المقاهي",
            description = "مجموعة فاخرة لعشاق القهوة المختصة تضم أداة استخلاص إسبريسو بضغط 18 بار، مطحنة تروس مخروطية من الفولاذ، إبريق تبخير، وفناجين سيراميك حرارية يدوية الصنع.",
            category = "المنزل والقهوة",
            price = 540.0,
            originalPrice = 690.0,
            rating = 4.9,
            reviewCount = 83,
            imageKey = "hero",
            badgeText = "هدية مثالية",
            isFavorite = false,
            isFlashSale = true,
            stockCount = 14,
            availableSizes = "الطقم الأساسي,الطقم الاحترافي الكامل",
            availableColors = "نحاسي عتيق,أسود مطفي,أخضر زمردي",
            specs = "الضغط: 18 بار احترافي|المطحنة: 38 درجة طحن دقيقة|هدية مجانية: 250 جرام بن إثيوبي مختص"
        ),
        ProductEntity(
            id = 6,
            title = "نظارة شمسية كلاسيكية بعدسات مستقطبة",
            brand = "فيجن أتيليه",
            subtitle = "حماية UV400 كاملة مع إطار خفيف من الأسيتات الإيطالي",
            description = "نظارة شمسية بتصميم هندسي عصري يناسب جميع الوجوه، مزودة بعدسات مستقطبة (Polarized) تمنع التوهج وتوفر رؤية فائقة الوضوح أثناء القيادة والتنقل.",
            category = "أزياء",
            price = 295.0,
            originalPrice = 390.0,
            rating = 4.6,
            reviewCount = 52,
            imageKey = "fashion",
            badgeText = null,
            isFavorite = false,
            isFlashSale = false,
            stockCount = 25,
            availableSizes = "قياسي (52-20),عريض (55-20)",
            availableColors = "بني متدرج,أسود كلاسيكي,إطار ذهبي بعدسات خضراء",
            specs = "العدسات: مستقطبة UV400|المفصلات: تيتانيوم مزدوج مرن|العلبة: جلد فاخر مع منديل مايكروفايبر"
        ),
        ProductEntity(
            id = 7,
            title = "مجموعة العناية بالبشرة بذهب عيار 24 والورد",
            brand = "لوميير بيوتي",
            subtitle = "سيروم الإشراقة الفورية وكريم الترطيب العميق الليلي",
            description = "تركيبة غنية بخلاصة الورد الطائفي النقي وحمض الهيالورونيك الثلاثي ورقائق الذهب التجميلي لمنح البشرة نضارة فورية وترطيباً عميقاً يدوم طوال اليوم.",
            category = "عطور وتجميل",
            price = 320.0,
            originalPrice = 430.0,
            rating = 4.8,
            reviewCount = 67,
            imageKey = "perfume",
            badgeText = "طبيعي 100%",
            isFavorite = false,
            isFlashSale = false,
            stockCount = 18,
            availableSizes = "حجم قياسي,حجم عائلي مضاعف",
            availableColors = "التركيبة الذهبية,تركيبة الورد المركز",
            specs = "المكونات: خالٍ من البارابين والعطور الصناعية|مناسب لـ: جميع أنواع البشرة|النتائج: ترطيب فوري خلال 7 أيام"
        ),
        ProductEntity(
            id = 8,
            title = "فواحة ذكية رخامية بالتحكم عبر التطبيق",
            brand = "أروما هوم",
            subtitle = "تبخير بارد بتقنية النانو لتعطير المساحات الواسعة بهدوء تام",
            description = "فواحة عطرية بتصميم منحوت من الحجر الطبيعي واللمسات النحاسية، تعمل بتقنية التبخير البارد بدون ماء للحفاظ على نقاء الزيوت العطرية وتغطية مساحة تصل إلى 120 متر مربع.",
            category = "المنزل والقهوة",
            price = 410.0,
            originalPrice = 530.0,
            rating = 4.9,
            reviewCount = 91,
            imageKey = "hero",
            badgeText = "الأعلى تقييماً",
            isFavorite = true,
            isFlashSale = false,
            stockCount = 10,
            availableSizes = "متوسط (60 م²),كبير (120 م²)",
            availableColors = "رخام أبيض عاجي,حجر ترافرتين رملي,أسود ملكي",
            specs = "التقنية: نانو بدون ماء أو حرارة|المؤقت: ذكي متعدد البرامج|مرفق: عبوة زيت عود وعنبر 50 مل مجاناً"
        )
    )

    fun getInitialReviews(): List<ReviewEntity> = listOf(
        ReviewEntity(
            productId = 1,
            authorName = "عبدالله المنصور",
            rating = 5,
            comment = "رائحة ملكية فخمة جداً وثباتها فوق الخيال! التغليف كان أنيقاً للغاية ويصلح كهدية فاخرة.",
            createdAt = System.currentTimeMillis() - 86400000L * 3
        ),
        ReviewEntity(
            productId = 1,
            authorName = "نورة العتيبي",
            rating = 5,
            comment = "من أجمل عطور العود والعنبر التي جربتها، فوحان راقٍ غير مزعج ووصلني خلال أقل من 24 ساعة.",
            createdAt = System.currentTimeMillis() - 86400000L * 1
        ),
        ReviewEntity(
            productId = 2,
            authorName = "فيصل الحربي",
            rating = 5,
            comment = "العزل الصوتي مذهل في الطائرة والمكتب، والخامات المعدنية تعطي إحساساً بالفخامة العالية.",
            createdAt = System.currentTimeMillis() - 86400000L * 4
        ),
        ReviewEntity(
            productId = 3,
            authorName = "سارة الدوسري",
            rating = 5,
            comment = "جودة الجلد الطبيعي رائعة واللون الجملي مطابق للصور تماماً، تتسع لجميع أغراضي اليومية بأناقة.",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        ReviewEntity(
            productId = 5,
            authorName = "ماجد الشهري",
            rating = 5,
            comment = "استخلاص الإسبريسو ممتاز بكريما غنية، والبن المرفق معها طعمه رائع جداً.",
            createdAt = System.currentTimeMillis() - 86400000L * 5
        )
    )

    fun getInitialAddresses(): List<AddressEntity> = listOf(
        AddressEntity(
            id = 1,
            label = "المنزل - الرياض",
            recipientName = "سلطان العتيبي",
            city = "الرياض",
            districtAndStreet = "حي الملقا، طريق أنس بن مالك، فيلا 24",
            phone = "0501234567",
            isDefault = true
        ),
        AddressEntity(
            id = 2,
            label = "المكتب - جدة",
            recipientName = "سلطان العتيبي",
            city = "جدة",
            districtAndStreet = "حي الشاطئ، برج الأعمال، الطابق 8",
            phone = "0559876543",
            isDefault = false
        )
    )
}
