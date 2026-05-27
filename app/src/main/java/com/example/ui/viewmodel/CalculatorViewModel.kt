package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationHistory
import com.example.data.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow

enum class CalculatorTab(val title: String) {
    GENERAL("General Calculator"),
    CURRENCY("Currency Converter"),
    UNIT("Unit Converter"),
    DISCOUNT("Discount Calculator"),
    TIP("Tip Calculator"),
    DATE("Date Calculator"),
    FUEL_COST("Fuel Cost Calculator"),
    FUEL_EFFICIENCY("Fuel Efficiency Calculator"),
    GPA("GPA Calculator"),
    BMI("Health (BMI)"),
    HEX("Hexadecimal"),
    LOAN("Loan (EMI)"),
    SALES_TAX("Sales Tax")
}

data class CourseEntry(val points: String = "", val credits: String = "")

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = HistoryRepository(database.historyDao())

    val historyState: StateFlow<List<CalculationHistory>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Tab
    private val _currentTab = MutableStateFlow(CalculatorTab.GENERAL)
    val currentTab: StateFlow<CalculatorTab> = _currentTab.asStateFlow()

    // Preferences & Theme
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null) // null = system default
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    // Show Tips Dialog State
    private val _showTipsDialog = MutableStateFlow(false)
    val showTipsDialog: StateFlow<Boolean> = _showTipsDialog.asStateFlow()

    // 1. GENERAL CALCULATOR STATE
    private val _generalExpression = MutableStateFlow("")
    val generalExpression: StateFlow<String> = _generalExpression.asStateFlow()

    private val _generalResult = MutableStateFlow("0")
    val generalResult: StateFlow<String> = _generalResult.asStateFlow()

    private val _generalHistory = MutableStateFlow("")
    val generalHistory: StateFlow<String> = _generalHistory.asStateFlow()

    // 2. CURRENCY CONVERTER STATE
    private val _currencyAmount = MutableStateFlow("1")
    val currencyAmount: StateFlow<String> = _currencyAmount.asStateFlow()

    private val _currencyFrom = MutableStateFlow("INR")
    val currencyFrom: StateFlow<String> = _currencyFrom.asStateFlow()

    private val _currencyTo = MutableStateFlow("USD")
    val currencyTo: StateFlow<String> = _currencyTo.asStateFlow()

    private val _currencyResult = MutableStateFlow("Result will appear here")
    val currencyResult: StateFlow<String> = _currencyResult.asStateFlow()

    val currencyRates = mapOf(
        "USD" to 1.0,
        "INR" to 83.5,
        "EUR" to 0.92,
        "GBP" to 0.79,
        "CAD" to 1.36,
        "JPY" to 156.4,
        "AUD" to 1.50
    )

    // 3. UNIT CONVERTER STATE
    private val _unitProperty = MutableStateFlow("Length") // Length, Weight, Temperature
    val unitProperty: StateFlow<String> = _unitProperty.asStateFlow()

    private val _unitValue = MutableStateFlow("1")
    val unitValue: StateFlow<String> = _unitValue.asStateFlow()

    private val _unitFrom = MutableStateFlow("Meter")
    val unitFrom: StateFlow<String> = _unitFrom.asStateFlow()

    private val _unitTo = MutableStateFlow("Kilometer")
    val unitTo: StateFlow<String> = _unitTo.asStateFlow()

    private val _unitResult = MutableStateFlow("Result will appear here")
    val unitResult: StateFlow<String> = _unitResult.asStateFlow()

    // 4. DISCOUNT CALCULATOR STATE
    private val _discountPrice = MutableStateFlow("")
    val discountPrice: StateFlow<String> = _discountPrice.asStateFlow()

    private val _discountPercentage = MutableStateFlow("")
    val discountPercentage: StateFlow<String> = _discountPercentage.asStateFlow()

    private val _discountResult = MutableStateFlow("Final Price & Savings info")
    val discountResult: StateFlow<String> = _discountResult.asStateFlow()

    // 5. TIP CALCULATOR STATE
    private val _tipBill = MutableStateFlow("")
    val tipBill: StateFlow<String> = _tipBill.asStateFlow()

    private val _tipPercentage = MutableStateFlow("15")
    val tipPercentage: StateFlow<String> = _tipPercentage.asStateFlow()

    private val _tipSplit = MutableStateFlow("1")
    val tipSplit: StateFlow<String> = _tipSplit.asStateFlow()

    private val _tipResult = MutableStateFlow("Split amount data")
    val tipResult: StateFlow<String> = _tipResult.asStateFlow()

    // 6. DATE CALCULATOR STATE
    private val _dateStart = MutableStateFlow("") // yyyy-MM-dd
    val dateStart: StateFlow<String> = _dateStart.asStateFlow()

    private val _dateEnd = MutableStateFlow("") // yyyy-MM-dd
    val dateEnd: StateFlow<String> = _dateEnd.asStateFlow()

    private val _dateResult = MutableStateFlow("Difference breakdown")
    val dateResult: StateFlow<String> = _dateResult.asStateFlow()

    // 7. FUEL COST CALCULATOR STATE
    private val _fcDistance = MutableStateFlow("")
    val fcDistance: StateFlow<String> = _fcDistance.asStateFlow()

    private val _fcPrice = MutableStateFlow("")
    val fcPrice: StateFlow<String> = _fcPrice.asStateFlow()

    private val _fcMileage = MutableStateFlow("")
    val fcMileage: StateFlow<String> = _fcMileage.asStateFlow()

    private val _fcResult = MutableStateFlow("Total expected expenditure")
    val fcResult: StateFlow<String> = _fcResult.asStateFlow()

    // 8. FUEL EFFICIENCY CALCULATOR STATE
    private val _feDistance = MutableStateFlow("")
    val feDistance: StateFlow<String> = _feDistance.asStateFlow()

    private val _feFuel = MutableStateFlow("")
    val feFuel: StateFlow<String> = _feFuel.asStateFlow()

    private val _feResult = MutableStateFlow("Efficiency breakdown")
    val feResult: StateFlow<String> = _feResult.asStateFlow()

    // 9. GPA CALCULATOR STATE
    val gpaCourses = mutableStateListOf(CourseEntry("", ""))

    private val _gpaResult = MutableStateFlow("Calculated Cumulative GPA")
    val gpaResult: StateFlow<String> = _gpaResult.asStateFlow()

    // 10. HEALTH CALCULATOR STATE (BMI)
    private val _bmiWeight = MutableStateFlow("")
    val bmiWeight: StateFlow<String> = _bmiWeight.asStateFlow()

    private val _bmiHeight = MutableStateFlow("")
    val bmiHeight: StateFlow<String> = _bmiHeight.asStateFlow()

    private val _bmiResult = MutableStateFlow("BMI Status and Index details")
    val bmiResult: StateFlow<String> = _bmiResult.asStateFlow()

    // 11. HEXADECIMAL CALCULATOR STATE
    private val _hexDec = MutableStateFlow("")
    val hexDec: StateFlow<String> = _hexDec.asStateFlow()

    private val _hexHex = MutableStateFlow("")
    val hexHex: StateFlow<String> = _hexHex.asStateFlow()

    private val _hexResult = MutableStateFlow("Bilateral translation status")
    val hexResult: StateFlow<String> = _hexResult.asStateFlow()

    // 12. LOAN CALCULATOR STATE
    private val _loanPrincipal = MutableStateFlow("")
    val loanPrincipal: StateFlow<String> = _loanPrincipal.asStateFlow()

    private val _loanRate = MutableStateFlow("")
    val loanRate: StateFlow<String> = _loanRate.asStateFlow()

    private val _loanMonths = MutableStateFlow("")
    val loanMonths: StateFlow<String> = _loanMonths.asStateFlow()

    private val _loanResult = MutableStateFlow("Monthly EMI Breakdown")
    val loanResult: StateFlow<String> = _loanResult.asStateFlow()

    // 13. SALES TAX CALCULATOR STATE
    private val _taxPrice = MutableStateFlow("")
    val taxPrice: StateFlow<String> = _taxPrice.asStateFlow()

    private val _taxRate = MutableStateFlow("")
    val taxRate: StateFlow<String> = _taxRate.asStateFlow()

    private val _taxResult = MutableStateFlow("Aggregated Tax amount calculations")
    val taxResult: StateFlow<String> = _taxResult.asStateFlow()


    // NAVIGATION & DRAWER ACTIONS
    fun selectTab(tab: CalculatorTab) {
        _currentTab.value = tab
    }

    fun setTheme(dark: Boolean?) {
        _isDarkTheme.value = dark
    }

    fun setTipsDialogVisible(visible: Boolean) {
        _showTipsDialog.value = visible
    }

    // 1. GENERAL CALCULATOR METHODS
    fun setGeneralExpression(expr: String) {
        _generalExpression.value = expr
    }

    fun pressGeneralKey(key: String) {
        when (key) {
            "C", "AC" -> {
                _generalExpression.value = ""
                _generalResult.value = "0"
                _generalHistory.value = ""
            }
            "DEL", "⌫" -> {
                val expr = _generalExpression.value
                if (expr.isNotEmpty()) {
                    _generalExpression.value = expr.dropLast(1)
                }
            }
            "=" -> {
                calculateGeneralResult()
            }
            else -> {
                val current = _generalExpression.value
                // If it is 0 and we type a digit, override
                if (current == "0" && key != "." && !isOperator(key)) {
                    _generalExpression.value = key
                } else {
                    _generalExpression.value = current + key
                }
            }
        }
    }

    private fun isOperator(s: String): Boolean {
        return s == "+" || s == "-" || s == "*" || s == "/" || s == "÷" || s == "×" || s == "%"
    }

    private fun calculateGeneralResult() {
        val rawExpr = _generalExpression.value.trim()
        if (rawExpr.isEmpty()) return
        val sanitizedExpr = rawExpr
            .replace('x', '*')
            .replace('X', '*')
            .replace('×', '*')
            .replace('✕', '*')
            .replace('÷', '/')
        try {
            val eval = ExpressionEvaluator().evaluate(sanitizedExpr)
            // Format output nicely (no .0 if integer)
            val resultFormatted = if (eval % 1.0 == 0.0) {
                eval.toLong().toString()
            } else {
                eval.toString()
            }
            _generalHistory.value = "$rawExpr ="
            _generalResult.value = resultFormatted

            // Insert into history repository in coroutine
            viewModelScope.launch {
                repository.insert(CalculationHistory(expression = rawExpr, result = resultFormatted))
            }
        } catch (e: Throwable) {
            _generalResult.value = "Error"
        }
    }

    fun deleteHistoryItem(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    fun applyHistoryToScreen(item: CalculationHistory) {
        _generalExpression.value = item.expression
        _generalResult.value = item.result
        _generalHistory.value = "${item.expression} ="
    }


    // 2. CURRENCY CONVERTER METHODS
    fun setCurrencyAmount(value: String) { _currencyAmount.value = value }
    fun setCurrencyFrom(value: String) { _currencyFrom.value = value }
    fun setCurrencyTo(value: String) { _currencyTo.value = value }
    fun convertCurrency() {
        val amt = _currencyAmount.value.toDoubleOrNull()
        if (amt == null) {
            _currencyResult.value = "Please enter a valid amount"
            return
        }
        val from = _currencyFrom.value
        val to = _currencyTo.value
        val rateFrom = currencyRates[from] ?: 1.0
        val rateTo = currencyRates[to] ?: 1.0

        val resultInUSD = amt / rateFrom
        val conversionResult = resultInUSD * rateTo
        _currencyResult.value = String.format(Locale.US, "%.4f %s = %.4f %s", amt, from, conversionResult, to)
    }


    // 3. UNIT CONVERTER METHODS
    fun setUnitProperty(value: String) {
        _unitProperty.value = value
        // Reset from and to units depending on the property chosen
        when (value) {
            "Length" -> {
                _unitFrom.value = "Meter"
                _unitTo.value = "Kilometer"
            }
            "Weight" -> {
                _unitFrom.value = "Kilogram"
                _unitTo.value = "Gram"
            }
            "Temperature" -> {
                _unitFrom.value = "Celsius"
                _unitTo.value = "Fahrenheit"
            }
        }
        _unitResult.value = "Result will appear here"
    }

    fun setUnitValue(value: String) { _unitValue.value = value }
    fun setUnitFrom(value: String) { _unitFrom.value = value }
    fun setUnitTo(value: String) { _unitTo.value = value }

    fun convertUnits() {
        val valDouble = _unitValue.value.toDoubleOrNull()
        if (valDouble == null) {
            _unitResult.value = "Please enter a valid value"
            return
        }
        val prop = _unitProperty.value
        val from = _unitFrom.value
        val to = _unitTo.value

        val result: Double
        if (prop == "Temperature") {
            val celsius = when (from) {
                "Celsius" -> valDouble
                "Fahrenheit" -> (valDouble - 32.0) * 5.0 / 9.0
                "Kelvin" -> valDouble - 273.15
                else -> valDouble
            }
            result = when (to) {
                "Celsius" -> celsius
                "Fahrenheit" -> (celsius * 9.0 / 5.0) + 32.0
                "Kelvin" -> celsius + 273.15
                else -> celsius
            }
        } else {
            val rates = if (prop == "Length") {
                mapOf(
                    "Meter" to 1.0,
                    "Kilometer" to 1000.0,
                    "Mile" to 1609.344,
                    "Foot" to 0.3048,
                    "Inch" to 0.0254,
                    "Centimeter" to 0.01
                )
            } else {
                mapOf(
                    "Kilogram" to 1.0,
                    "Gram" to 0.001,
                    "Pound" to 0.45359237,
                    "Ounce" to 0.028349523
                )
            }
            val rateFrom = rates[from] ?: 1.0
            val rateTo = rates[to] ?: 1.0
            val baseValue = valDouble * rateFrom
            result = baseValue / rateTo
        }

        _unitResult.value = String.format(Locale.US, "%.5f %s = %.5f %s", valDouble, from, result, to)
    }


    // 4. DISCOUNT CALCULATOR METHODS
    fun setDiscountPrice(value: String) { _discountPrice.value = value }
    fun setDiscountPercentage(value: String) { _discountPercentage.value = value }
    fun calculateDiscount() {
        val price = _discountPrice.value.toDoubleOrNull()
        val percent = _discountPercentage.value.toDoubleOrNull()
        if (price == null || percent == null || price < 0 || percent < 0 || percent > 100) {
            _discountResult.value = "Please enter a valid price and percentage (0 to 100)"
            return
        }
        val savings = price * (percent / 100.0)
        val finalPrice = price - savings
        _discountResult.value = String.format(Locale.US, "Final Price: $%.2f\nTotal Saved: $%.2f", finalPrice, savings)
    }


    // 5. TIP CALCULATOR METHODS
    fun setTipBill(value: String) { _tipBill.value = value }
    fun setTipPercentage(value: String) { _tipPercentage.value = value }
    fun setTipSplit(value: String) { _tipSplit.value = value }
    fun calculateTip() {
        val bill = _tipBill.value.toDoubleOrNull()
        val percent = _tipPercentage.value.toDoubleOrNull()
        val split = _tipSplit.value.toIntOrNull()

        if (bill == null || percent == null || split == null || bill < 0 || percent < 0 || split <= 0) {
            _tipResult.value = "Please enter valid bill, tip %, and split number of people (> 0)"
            return
        }

        val tipAmount = bill * (percent / 100.0)
        val totalAmount = bill + tipAmount
        val perPerson = totalAmount / split
        _tipResult.value = String.format(
            Locale.US,
            "Total Tip: $%.2f\nTotal Payable: $%.2f\nPer Person Split: $%.2f",
            tipAmount, totalAmount, perPerson
        )
    }


    // 6. DATE CALCULATOR METHODS
    fun setDateStart(value: String) { _dateStart.value = value }
    fun setDateEnd(value: String) { _dateEnd.value = value }
    fun calculateDateDiff() {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val dStart = format.parse(_dateStart.value)
            val dEnd = format.parse(_dateEnd.value)
            if (dStart == null || dEnd == null) {
                _dateResult.value = "Select valid start & end dates"
                return
            }
            val diffMs = abs(dEnd.time - dStart.time)
            val diffDays = diffMs / (1000 * 60 * 60 * 24)
            val weeks = diffDays / 7
            val remainingDays = diffDays % 7

            _dateResult.value = String.format(
                Locale.US,
                "Total Days: %d\nWhich equals: %d Weeks and %d Days",
                diffDays, weeks, remainingDays
            )
        } catch (e: Exception) {
            _dateResult.value = "Please select both dates using format YYYY-MM-DD"
        }
    }


    // 7. FUEL COST CALCULATOR METHODS
    fun setFcDistance(value: String) { _fcDistance.value = value }
    fun setFcPrice(value: String) { _fcPrice.value = value }
    fun setFcMileage(value: String) { _fcMileage.value = value }
    fun calculateFuelCost() {
        val dist = _fcDistance.value.toDoubleOrNull()
        val price = _fcPrice.value.toDoubleOrNull()
        val mileage = _fcMileage.value.toDoubleOrNull()

        if (dist == null || price == null || mileage == null || dist < 0 || price < 0 || mileage <= 0) {
            _fcResult.value = "Please enter positive numbers. Efficiency must be > 0"
            return
        }

        val totalCost = (dist / mileage) * price
        _fcResult.value = String.format(Locale.US, "Aggregated Fuel Expense: $%.2f\nFuel Consumed: %.2f units", totalCost, dist / mileage)
    }


    // 8. FUEL EFFICIENCY CALCULATOR METHODS
    fun setFeDistance(value: String) { _feDistance.value = value }
    fun setFeFuel(value: String) { _feFuel.value = value }
    fun calculateFuelEfficiency() {
        val dist = _feDistance.value.toDoubleOrNull()
        val fuel = _feFuel.value.toDoubleOrNull()

        if (dist == null || fuel == null || dist < 0 || fuel <= 0) {
            _feResult.value = "Enter positive numbers. Fuel used must be > 0"
            return
        }

        val efficiency = dist / fuel
        _feResult.value = String.format(Locale.US, "Efficiency: %.2f units per fuel vol (e.g., KM/L or MPG)", efficiency)
    }


    // 9. GPA CALCULATOR METHODS
    fun addGpaRow() {
        gpaCourses.add(CourseEntry("", ""))
    }

    fun removeGpaRow(index: Int) {
        if (gpaCourses.size > 1) {
            gpaCourses.removeAt(index)
        }
    }

    fun updateGpaRow(index: Int, points: String, credits: String) {
        if (index in gpaCourses.indices) {
            gpaCourses[index] = CourseEntry(points, credits)
        }
    }

    fun calculateGPA() {
        var totalPointsEarned = 0.0
        var totalCredits = 0.0
        var hasValidData = false

        for (course in gpaCourses) {
            val p = course.points.toDoubleOrNull()
            val c = course.credits.toDoubleOrNull()
            if (p != null && c != null && p >= 0 && c > 0) {
                totalPointsEarned += (p * c)
                totalCredits += c
                hasValidData = true
            }
        }

        if (!hasValidData || totalCredits <= 0.0) {
            _gpaResult.value = "Enter valid Grade Points & Credits"
            return
        }

        val gpaVal = totalPointsEarned / totalCredits
        _gpaResult.value = String.format(Locale.US, "Calculated Semester GPA: %.2f\n(Total Credits: %.1f)", gpaVal, totalCredits)
    }


    // 10. HEALTH CALCULATOR (BMI) METHODS
    fun setBmiWeight(value: String) { _bmiWeight.value = value }
    fun setBmiHeight(value: String) { _bmiHeight.value = value }
    fun calculateBMI() {
        val w = _bmiWeight.value.toDoubleOrNull()
        val h = _bmiHeight.value.toDoubleOrNull()

        if (w == null || h == null || w <= 0 || h <= 0) {
            _bmiResult.value = "Please enter valid dynamic height and weight"
            return
        }

        val heightInMeters = h / 100.0
        val bmi = w / (heightInMeters * heightInMeters)
        val category = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal weight"
            bmi < 30.0 -> "Overweight"
            else -> "Obese"
        }

        _bmiResult.value = String.format(Locale.US, "BMI Index: %.1f\nStatus: %s", bmi, category)
    }


    // 11. HEXADECIMAL CALCULATOR METHODS
    fun updateHexFromDec(decStr: String) {
        _hexDec.value = decStr
        val decVal = decStr.toLongOrNull()
        if (decVal != null) {
            _hexHex.value = decVal.toString(16).uppercase()
            _hexResult.value = "Base conversion up to date."
        } else {
            _hexHex.value = ""
            if (decStr.isEmpty()) {
                _hexResult.value = "Bilateral translation status"
            } else {
                _hexResult.value = "Invalid Decimal"
            }
        }
    }

    fun updateDecFromHex(hexStr: String) {
        _hexHex.value = hexStr
        try {
            val decVal = hexStr.lowercase().toLong(16)
            _hexDec.value = decVal.toString()
            _hexResult.value = "Base conversion up to date."
        } catch (e: Exception) {
            _hexDec.value = ""
            if (hexStr.isEmpty()) {
                _hexResult.value = "Bilateral translation status"
            } else {
                _hexResult.value = "Invalid Hexadecimal"
            }
        }
    }


    // 12. LOAN CALCULATOR METHODS
    fun setLoanPrincipal(value: String) { _loanPrincipal.value = value }
    fun setLoanRate(value: String) { _loanRate.value = value }
    fun setLoanMonths(value: String) { _loanMonths.value = value }
    fun calculateEMI() {
        val p = _loanPrincipal.value.toDoubleOrNull()
        val rateAnnual = _loanRate.value.toDoubleOrNull()
        val n = _loanMonths.value.toIntOrNull()

        if (p == null || rateAnnual == null || n == null || p <= 0 || rateAnnual < 0 || n <= 0) {
            _loanResult.value = "Configure valid Principal, Rate % per annum, and Months (> 0)"
            return
        }

        if (rateAnnual == 0.0) {
            val emi = p / n
            _loanResult.value = String.format(Locale.US, "Monthly EMI: $%.2f\nTotal Interest: $0.00\nTotal Payment: $%.2f", emi, p)
            return
        }

        val r = (rateAnnual / 12.0) / 100.0
        val emi = (p * r * (1 + r).pow(n.toDouble())) / ((1 + r).pow(n.toDouble()) - 1)
        val totalPayment = emi * n
        val totalInterest = totalPayment - p

        _loanResult.value = String.format(
            Locale.US,
            "Monthly EMI: $%.2f\nTotal Interest: $%.2f\nTotal Payment: $%.2f",
            emi, totalInterest, totalPayment
        )
    }


    // 13. SALES TAX CALCULATOR METHODS
    fun setTaxPrice(value: String) { _taxPrice.value = value }
    fun setTaxRate(value: String) { _taxRate.value = value }
    fun calculateSalesTax() {
        val p = _taxPrice.value.toDoubleOrNull()
        val r = _taxRate.value.toDoubleOrNull()

        if (p == null || r == null || p < 0 || r < 0) {
            _taxResult.value = "Enter valid Net Base Price and Tax Rate %"
            return
        }

        val taxAmount = p * (r / 100.0)
        val grossPrice = p + taxAmount

        _taxResult.value = String.format(Locale.US, "Tax Paid: $%.2f\nGross Price total: $%.2f", taxAmount, grossPrice)
    }
}


// Token and parsing recursion algorithm
class ExpressionEvaluator {
    fun evaluate(expression: String): Double {
        val sanitized = expression.replace(" ", "")
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < sanitized.length) sanitized[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < sanitized.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) {
                        val d = parseFactor()
                        if (d == 0.0) throw ArithmeticException("Division by zero")
                        x /= d // division
                    }
                    else return x
                }
            }

             fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus

                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    eat(')'.code)
                } else if ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) { // numbers
                    while ((ch >= '0'.code && ch <= '9'.code) || ch == '.'.code) nextChar()
                    val sub = sanitized.substring(startPos, pos)
                    x = sub.toDouble()
                } else {
                    throw RuntimeException("Unexpected character: " + ch.toChar())
                }

                while (eat('%'.code)) {
                    x /= 100.0
                }

                return x
            }
        }.parse()
    }
}
