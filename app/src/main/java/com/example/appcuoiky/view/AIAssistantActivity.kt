package com.example.appcuoiky.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcuoiky.R
import com.example.appcuoiky.adapter.ChatAdapter
import com.example.appcuoiky.model.ChatMessage
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import java.text.NumberFormat
import java.util.*

class AIAssistantActivity : AppCompatActivity() {

    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var editTextMessage: TextInputEditText
    private lateinit var fabSend: FloatingActionButton
    private lateinit var emptyStateLayout: View
    private lateinit var loadingLayout: View
    private lateinit var chatAdapter: ChatAdapter

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private var monthlyIncome: Double = 0.0
    private var monthlyExpense: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        monthlyIncome = intent.getDoubleExtra("monthly_income", 0.0)
        monthlyExpense = intent.getDoubleExtra("monthly_expense", 0.0)

        initViews()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews() {
        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        editTextMessage = findViewById(R.id.editTextMessage)
        fabSend = findViewById(R.id.fabSend)
        emptyStateLayout = findViewById(R.id.emptyStateLayout)
        loadingLayout = findViewById(R.id.loadingLayout)

        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).apply {
            setNavigationOnClickListener { finish() }
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        recyclerViewChat.apply {
            layoutManager = LinearLayoutManager(this@AIAssistantActivity)
            adapter = chatAdapter
        }
    }

    private fun setupListeners() {
        fabSend.setOnClickListener { sendMessage() }

        editTextMessage.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage()
                true
            } else false
        }

        findViewById<Chip>(R.id.chipQuestion1).setOnClickListener {
            sendQuickQuestion("Phân tích tình hình tài chính của tôi")
        }
        findViewById<Chip>(R.id.chipQuestion2).setOnClickListener {
            sendQuickQuestion("Làm sao để thoát khỏi nợ nần?")
        }
        findViewById<Chip>(R.id.chipQuestion3).setOnClickListener {
            sendQuickQuestion("Tôi nên bắt đầu đầu tư như thế nào?")
        }
    }

    private fun sendQuickQuestion(question: String) {
        editTextMessage.setText(question)
        sendMessage()
    }

    private fun sendMessage() {
        val message = editTextMessage.text.toString().trim()
        if (message.isEmpty()) return

        emptyStateLayout.visibility = View.GONE

        chatAdapter.addMessage(ChatMessage(message, true))
        recyclerViewChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
        editTextMessage.text?.clear()

        loadingLayout.visibility = View.VISIBLE

        scope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
                delay(Random().nextLong(500, 1000))
            }

            val response = getOfflineResponse(message)

            loadingLayout.visibility = View.GONE
            chatAdapter.addMessage(ChatMessage(response, false))
            recyclerViewChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
        }
    }

    private fun getOfflineResponse(userMessage: String): String {
        val msg = userMessage.lowercase(Locale.getDefault())
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))

        return when {
            msg.contains("phân tích") || msg.contains("tình hình") || msg.contains("tổng kết") || msg.contains("báo cáo") || msg.contains("chi tiêu") -> {
                analyzeFinancialHealth(formatter)
            }

            msg.contains("kế hoạch") || msg.contains("ngân sách") || msg.contains("quy tắc") || msg.contains("chia tiền") || msg.contains("quản lý") -> {
                getBudgetingRules()
            }

            msg.contains("tiết kiệm") || msg.contains("giữ tiền") || msg.contains("mua sắm") || msg.contains("sale") || msg.contains("shopping") -> {
                getSavingAndShoppingTips()
            }

            msg.contains("nợ") || msg.contains("vay") || msg.contains("trả góp") || msg.contains("lãi") -> {
                getDebtManagementAdvice()
            }

            msg.contains("đầu tư") || msg.contains("sinh lời") || msg.contains("chứng khoán") || msg.contains("vàng") || msg.contains("đất") -> {
                getInvestmentAdvice()
            }

            msg.contains("khẩn cấp") || msg.contains("dự phòng") || msg.contains("rủi ro") || msg.contains("ốm đau") -> {
                getEmergencyFundAdvice()
            }

            msg.contains("kiếm tiền") || msg.contains("tăng thu") || msg.contains("lương") || msg.contains("giàu") || msg.contains("thu nhập") -> {
                getIncomeGrowthAdvice()
            }

            msg.contains("chào") || msg.contains("hello") || msg.contains("hi") -> {
                "👋 Xin chào! Tôi là Trợ lý Tài chính Okela.\nTôi ở đây để giúp bạn quản lý tiền nong hiệu quả hơn. Bạn đang lo lắng về vấn đề gì? (Tiết kiệm, nợ nần, hay đầu tư?)"
            }
            msg.contains("cảm ơn") || msg.contains("thank") -> {
                "🥰 Không có chi! Rất vui được giúp bạn. Hãy nhớ ghi chép chi tiêu đều đặn nhé!"
            }

            msg.contains("thời tiết") || msg.contains("bóng đá") || msg.contains("yêu") || msg.contains("ăn gì") || msg.contains("nhạc") || msg.contains("phim") -> {
                "😅 Xin lỗi bạn ơi, tôi là trợ lý tài chính nên hơi khô khan chút.\nTôi chỉ biết trả lời về **Tiền, Tiết kiệm, Đầu tư và Chi tiêu** thôi ạ. Đừng hỏi khó tôi nhé! 🙏"
            }

            else -> {
                """
                🤔 Tôi chưa hiểu rõ ý bạn lắm. Bạn có thể hỏi cụ thể hơn không?
                
                Ví dụ:
                👉 "Phân tích tài chính tháng này"
                👉 "Làm sao để trả hết nợ?"
                👉 "Quy tắc 50/30/20 là gì?"
                👉 "Nên tiết kiệm bao nhiêu tiền?"
                👉 "Đầu tư gì bây giờ?"
                """.trimIndent()
            }
        }
    }

    private fun analyzeFinancialHealth(formatter: NumberFormat): String {
        val balance = monthlyIncome - monthlyExpense
        val savingsRate = if (monthlyIncome > 0) ((balance / monthlyIncome) * 100).toInt() else 0

        val statusEmoji: String
        val advice: String

        if (monthlyIncome == 0.0 && monthlyExpense == 0.0) {
            return "📉 **Chưa có dữ liệu!**\nHiện tại tôi chưa thấy bạn nhập khoản thu chi nào. Hãy nhập giao dịch để tôi có thể phân tích nhé!"
        }

        when {
            balance < 0 -> {
                statusEmoji = "🚨"
                advice = """
                    **CẢNH BÁO ĐỎ:** Bạn đang chi tiêu vượt quá thu nhập (Âm ${formatter.format(balance * -1)}).
                    
                    👉 **Hành động ngay:**
                    1. Dừng ngay các khoản mua sắm không thiết yếu (quần áo, trà sữa, đi chơi).
                    2. Kiểm tra lại xem tiền đã đi đâu nhiều nhất?
                    3. Cân nhắc vay mượn người thân thay vì vay lãi cao để bù đắp.
                """.trimIndent()
            }
            savingsRate < 10 -> {
                statusEmoji = "⚠️"
                advice = """
                    **CẦN CẢI THIỆN:** Tỷ lệ tiết kiệm $savingsRate% là khá thấp (Mức an toàn là 10-20%).
                    
                    👉 **Lời khuyên:**
                    • Hãy thử thách bản thân: "Tuần không tiêu tiền vặt".
                    • Cắt giảm 1 thói quen tốn kém (ví dụ: tự nấu ăn thay vì gọi ship).
                """.trimIndent()
            }
            savingsRate < 30 -> {
                statusEmoji = "✅"
                advice = """
                    **KHÁ TỐT:** Bạn đang tiết kiệm được $savingsRate% thu nhập.
                    
                    👉 **Lời khuyên:**
                    • Hãy duy trì phong độ này!
                    • Số tiền dư này nên được chia vào Quỹ khẩn cấp hoặc Đầu tư ngay.
                """.trimIndent()
            }
            else -> {
                statusEmoji = "🌟"
                advice = """
                    **XUẤT SẮC:** Bạn tiết kiệm được tới $savingsRate% thu nhập! Khả năng quản lý tài chính của bạn rất tuyệt vời.
                    
                    👉 **Lời khuyên:**
                    • Với số dư lớn thế này, đừng để tiền nằm im. Hãy tìm hiểu về Đầu tư để tiền đẻ ra tiền nhé!
                """.trimIndent()
            }
        }

        return """
            $statusEmoji **PHÂN TÍCH TÀI CHÍNH THÁNG NÀY**
            
            💵 **Thu nhập:** ${formatter.format(monthlyIncome)}
            💸 **Chi tiêu:** ${formatter.format(monthlyExpense)}
            💰 **Số dư:** ${formatter.format(balance)}
            📊 **Tỷ lệ tiết kiệm:** $savingsRate%
            
            ----------------------------------
            $advice
        """.trimIndent()
    }

    private fun getBudgetingRules(): String {
        return """
            🎯 **CÁC PHƯƠNG PHÁP LẬP NGÂN SÁCH HIỆU QUẢ**
            
            1️⃣ **Quy tắc 50/30/20 (Kinh điển):**
            • **50% Nhu cầu thiết yếu:** Tiền nhà, điện nước, ăn uống, xăng xe.
            • **30% Mong muốn cá nhân:** Mua sắm, giải trí, du lịch, cafe.
            • **20% Tiết kiệm & Đầu tư:** Trả nợ, quỹ khẩn cấp, hưu trí.
            
            2️⃣ **Quy tắc 6 Chiếc Lọ (JARS):**
            • 55% Chi tiêu thiết yếu.
            • 10% Tiết kiệm dài hạn.
            • 10% Giáo dục (học kỹ năng mới).
            • 10% Hưởng thụ (nuông chiều bản thân).
            • 10% Tự do tài chính (đầu tư).
            • 5% Từ thiện/Giúp đỡ người khác.
            
            💡 *Bạn hãy chọn một phương pháp phù hợp nhất với mình và bắt đầu ngay hôm nay nhé!*
        """.trimIndent()
    }

    private fun getSavingAndShoppingTips(): String {
        return """
            🛍️ **BÍ KÍP TIẾT KIỆM & MUA SẮM THÔNG MINH**
            
            1. **Quy tắc 24 Giờ:** ⏳
               Khi thích một món đồ đắt tiền, hãy đợi 24h (hoặc 3 ngày) rồi mới mua. 80% trường hợp bạn sẽ nhận ra mình không cần nó nữa.
            
            2. **Hiệu ứng Latte (Latte Factor):** ☕
               50k tiền cafe mỗi ngày = 1.5 triệu/tháng = 18 triệu/năm!
               👉 Hãy thử tự pha đồ uống hoặc mang cơm trưa đi làm.
            
            3. **Đi siêu thị với cái bụng no:** 🛒
               Đừng đi mua sắm khi đói, bạn sẽ có xu hướng mua nhiều thực phẩm hơn mức cần thiết. Luôn mang theo danh sách cần mua.
            
            4. **Hủy các gói đăng ký "Ma":** 👻
               Kiểm tra lại Netflix, Spotify, Gym... Nếu bạn không dùng nó trong 1 tháng qua, hãy hủy ngay.
        """.trimIndent()
    }

    private fun getDebtManagementAdvice(): String {
        return """
            💸 **CHIẾN LƯỢC TRẢ NỢ THÔNG MINH**
            
            Nợ nần là rào cản lớn nhất của tự do tài chính. Hãy thử 2 cách sau:
            
            ❄️ **1. Phương pháp Tuyết Lăn (Snowball):**
            • Liệt kê tất cả khoản nợ.
            • Trả khoản nợ **NHỎ NHẤT** trước tiên (bất kể lãi suất).
            • **Ưu điểm:** Tạo động lực tâm lý mạnh mẽ khi thấy các khoản nợ biến mất dần.
            
            🔥 **2. Phương pháp Tuyết Lở (Avalanche):**
            • Trả khoản nợ có **LÃI SUẤT CAO NHẤT** trước.
            • **Ưu điểm:** Tiết kiệm được nhiều tiền lãi nhất về lâu dài.
            
            ⚠️ **Lưu ý:** Tuyệt đối không vay thêm nợ mới để trả nợ cũ (trừ khi lãi suất thấp hơn hẳn).
        """.trimIndent()
    }

    private fun getInvestmentAdvice(): String {
        return """
            🌱 **NHẬP MÔN ĐẦU TƯ CHO NGƯỜI MỚI**
            
            "Đừng để tiền ngủ quên trong khi bạn đang làm việc!"
            
            1. **Gửi Tiết Kiệm:** 🏦
               • An toàn nhất, rủi ro thấp.
               • Phù hợp cho quỹ khẩn cấp hoặc mục tiêu ngắn hạn.
            
            2. **Vàng:** 🏆
               • Kênh trú ẩn an toàn chống lạm phát.
               • Nên mua tích trữ dài hạn.
            
            3. **Chứng khoán / Cổ phiếu:** 📈
               • Lợi nhuận cao nhưng rủi ro cao.
               • **Quy tắc:** Không bao giờ đầu tư vào thứ bạn không hiểu. Hãy học kiến thức cơ bản trước.
            
            4. **Đầu tư vào bản thân:** 🧠
               • Học ngoại ngữ, kỹ năng nghề nghiệp. Đây là khoản đầu tư sinh lời cao nhất!
               
            🚫 **Cấm kỵ:** Tránh xa các lời mời gọi "làm giàu nhanh", "lãi suất 30%/tháng"... đó thường là lừa đảo.
        """.trimIndent()
    }

    private fun getEmergencyFundAdvice(): String {
        return """
            🚨 **QUỸ KHẨN CẤP LÀ GÌ?**
            
            Là khoản tiền chỉ dùng khi... trời sập (mất việc, ốm đau, hỏng xe). Không dùng để đi du lịch hay mua sắm!
            
            💰 **Cần bao nhiêu là đủ?**
            Nên tích lũy đủ **3 đến 6 tháng** chi phí sinh hoạt tối thiểu.
            *Ví dụ: Bạn tiêu 5tr/tháng -> Quỹ cần 15tr - 30tr.*
            
            👉 **Bắt đầu thế nào?**
            Mỗi tháng trích 5-10% lương bỏ vào một tài khoản ngân hàng riêng biệt, đừng làm thẻ ATM cho tài khoản đó để tránh "ngứa tay" rút ra.
        """.trimIndent()
    }

    private fun getIncomeGrowthAdvice(): String {
        return """
            🚀 **CÁCH TĂNG THU NHẬP HIỆU QUẢ**
            
            Tiết kiệm có giới hạn, nhưng khả năng kiếm tiền là vô hạn!
            
            1. **Nâng cao chuyên môn:** Trở thành chuyên gia trong lĩnh vực của bạn để deal lương cao hơn.
            2. **Nghề tay trái (Side Hustle):** Bán hàng online, làm Freelancer, Grab, gia sư... tận dụng thời gian rảnh.
            3. **Thanh lý đồ cũ:** Bán những món đồ bạn không dùng nữa trên Facebook, Chợ Tốt. Vừa có tiền vừa dọn nhà gọn gàng.
            
            💪 *Hãy nhớ: Đừng đợi cơ hội đến, hãy tự tạo ra nó!*
        """.trimIndent()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}