package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.CalculatorTab
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.ui.viewmodel.CourseEntry
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorAppScreen(viewModel: CalculatorViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isDarkThemeState by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val showTipsDialog by viewModel.showTipsDialog.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Adaptive Check (Wide screen standard)
    val configuration = LocalConfiguration.current
    val isWideScreen = configuration.screenWidthDp >= 750

    // Navigation Items mapped to their display details
    val menuItems = listOf(
        NavigationItem(CalculatorTab.GENERAL, Icons.Default.Calculate, "General calculations with custom parser"),
        NavigationItem(CalculatorTab.CURRENCY, Icons.Default.AttachMoney, "Rate conversion between USD, INR, EUR, etc."),
        NavigationItem(CalculatorTab.UNIT, Icons.Default.Straighten, "Convert lengths, weights, & temperatures"),
        NavigationItem(CalculatorTab.DISCOUNT, Icons.Default.LocalOffer, "Price savings and dynamic rates"),
        NavigationItem(CalculatorTab.TIP, Icons.Default.RoomService, "Splits and total amount calculations"),
        NavigationItem(CalculatorTab.DATE, Icons.Default.CalendarToday, "Find interval duration in days and weeks"),
        NavigationItem(CalculatorTab.FUEL_COST, Icons.Default.LocalGasStation, "Calculate distance costs based on vehicle mileage"),
        NavigationItem(CalculatorTab.FUEL_EFFICIENCY, Icons.Default.Speed, "Check KM/L or MPG calculations"),
        NavigationItem(CalculatorTab.GPA, Icons.Default.School, "Calculate semester cumulative GPA"),
        NavigationItem(CalculatorTab.BMI, Icons.Default.Favorite, "Health and Body Mass Index category"),
        NavigationItem(CalculatorTab.HEX, Icons.Default.Code, "Decimal to hexadecimal bilateral conversion"),
        NavigationItem(CalculatorTab.LOAN, Icons.Default.AccountBalance, "Amortized EMI loans installments"),
        NavigationItem(CalculatorTab.SALES_TAX, Icons.AutoMirrored.Filled.ReceiptLong, "Dynamic base sales tax check")
    )

    // Tips Modal Dialog
    if (showTipsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setTipsDialogVisible(false) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.setTipsDialogVisible(false) },
                    modifier = Modifier.testTag("tips_confirm_button")
                ) {
                    Text("Got it")
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Calculator Hub Tips")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("• Use the drawer menu to swap instantly between 13 specialized calculators.", fontSize = 14.sp)
                    Text("• Ensure all numeric values inputted are mathematical numbers.", fontSize = 14.sp)
                    Text("• General Calculator saves calculation queries to local Room history.", fontSize = 14.sp)
                }
            },
            modifier = Modifier.testTag("tips_dialog")
        )
    }

    // Modal Drawer content
    val drawerContent: @Composable () -> Unit = {
        ModalDrawerSheet(
            modifier = Modifier
                .width(290.dp)
                .fillMaxHeight(),
            drawerContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculator Hub",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = {
                        scope.launch { drawerState.close() }
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Menu")
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                item {
                    // System actions
                    Column {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text("Toggle Visual Theme") },
                            selected = false,
                            onClick = {
                                viewModel.setTheme(!(isDarkThemeState ?: false))
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .testTag("theme_toggle_item")
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.HelpOutline, contentDescription = null) },
                            label = { Text("Usage Tips") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                viewModel.setTipsDialogVisible(true)
                            },
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .testTag("tips_toggle_item")
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calculators List",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(menuItems) { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = {
                            Column {
                                Text(item.tab.title, fontWeight = FontWeight.Bold)
                                Text(
                                    item.description,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        selected = currentTab == item.tab,
                        onClick = {
                            viewModel.selectTab(item.tab)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                            .testTag("nav_item_${item.tab.name.lowercase()}")
                    )
                }
            }
        }
    }

    if (isWideScreen) {
        // Tablet / Dual Pane layout (Permanent sidebar)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sidebar Panel
            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 1.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Spacer(modifier = Modifier.statusBarsPadding())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Calculator Hub",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row {
                            IconButton(
                                onClick = { viewModel.setTheme(!(isDarkThemeState ?: false)) },
                                modifier = Modifier.testTag("theme_button")
                            ) {
                                Icon(
                                    imageVector = if (isDarkThemeState == true) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Theme Choice"
                                )
                            }
                            IconButton(
                                onClick = { viewModel.setTipsDialogVisible(true) },
                                modifier = Modifier.testTag("tips_button")
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = "About Apps")
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(menuItems) { item ->
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = {
                                    Column {
                                        Text(item.tab.title, fontWeight = FontWeight.Bold)
                                        Text(
                                            item.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                selected = currentTab == item.tab,
                                onClick = { viewModel.selectTab(item.tab) },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 2.dp)
                                    .testTag("tablet_nav_${item.tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            VerticalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Current Calculator container Screen (Fluid alignment to avoid awkward stretching)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.TopCenter
            ) {
                val isGeneral = currentTab == CalculatorTab.GENERAL
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isGeneral) Color.Black else Color.Transparent)
                        .widthIn(max = if (isGeneral) 450.dp else 750.dp)
                        .padding(if (isGeneral) 0.dp else 16.dp)
                ) {
                    ActiveCalculatorContent(currentTab, viewModel)
                }
            }
        }
    } else {
        // Compact Mobile Screen (Modal swipe mechanism)
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = drawerContent,
            modifier = Modifier.fillMaxSize()
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                currentTab.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                },
                                modifier = Modifier.testTag("hamburger_menu_button")
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = "Hamburger options list")
                            }
                        },
                        actions = {
                            IconButton(onClick = { viewModel.setTheme(!(isDarkThemeState ?: false)) }) {
                                Icon(
                                    imageVector = if (isDarkThemeState == true) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = "Adjust Dark Lights"
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = if (currentTab == CalculatorTab.GENERAL) Color.Black else MaterialTheme.colorScheme.surface,
                            titleContentColor = if (currentTab == CalculatorTab.GENERAL) Color.White else MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = if (currentTab == CalculatorTab.GENERAL) Color.White else MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = if (currentTab == CalculatorTab.GENERAL) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    )
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                val isGeneral = currentTab == CalculatorTab.GENERAL
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(if (isGeneral) Color.Black else Color.Transparent)
                        .padding(innerPadding)
                        .padding(if (isGeneral) 0.dp else 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    ActiveCalculatorContent(currentTab, viewModel)
                }
            }
        }
    }
}

@Composable
fun ActiveCalculatorContent(tab: CalculatorTab, viewModel: CalculatorViewModel) {
    Crossfade(targetState = tab, label = "TabRotation", animationSpec = spring()) { currentTab ->
        when (currentTab) {
            CalculatorTab.GENERAL -> GeneralCalculatorView(viewModel)
            CalculatorTab.CURRENCY -> CurrencyConverterView(viewModel)
            CalculatorTab.UNIT -> UnitConverterView(viewModel)
            CalculatorTab.DISCOUNT -> DiscountCalculatorView(viewModel)
            CalculatorTab.TIP -> TipCalculatorView(viewModel)
            CalculatorTab.DATE -> DateCalculatorView(viewModel)
            CalculatorTab.FUEL_COST -> FuelCostCalculatorView(viewModel)
            CalculatorTab.FUEL_EFFICIENCY -> FuelEfficiencyCalculatorView(viewModel)
            CalculatorTab.GPA -> GpaCalculatorView(viewModel)
            CalculatorTab.BMI -> BmiCalculatorView(viewModel)
            CalculatorTab.HEX -> HexCalculatorView(viewModel)
            CalculatorTab.LOAN -> LoanCalculatorView(viewModel)
            CalculatorTab.SALES_TAX -> SalesTaxCalculatorView(viewModel)
        }
    }
}

// 1. GENERAL CALCULATOR VIEW
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GeneralCalculatorView(viewModel: CalculatorViewModel) {
    var showHistoryPanel by remember { mutableStateOf(false) }

    val expr by viewModel.generalExpression.collectAsStateWithLifecycle()
    val result by viewModel.generalResult.collectAsStateWithLifecycle()
    val historyText by viewModel.generalHistory.collectAsStateWithLifecycle()
    val dbHistory by viewModel.historyState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        if (showHistoryPanel) {
            // Room DB Calculations Panel toggled
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF1C1C1E), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("History Logs", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color.White)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { viewModel.clearAllHistory() },
                            modifier = Modifier.testTag("clear_all_history_btn")
                        ) {
                            Text("Clear All", color = Color(0xFFFF453A))
                        }
                        IconButton(onClick = { showHistoryPanel = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Exit history view", tint = Color.White)
                        }
                    }
                }

                if (dbHistory.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            "No computation histories standardly log yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF8E8E93)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(dbHistory, key = { it.id }) { item ->
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.applyHistoryToScreen(item)
                                        showHistoryPanel = false
                                    }
                                    .testTag("history_card_${item.id}"),
                                colors = CardDefaults.outlinedCardColors(
                                    containerColor = Color(0xFF2C2C2E),
                                    contentColor = Color.White
                                ),
                                border = CardDefaults.outlinedCardBorder(enabled = false)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.expression, fontSize = 14.sp, color = Color(0xFF8E8E93))
                                        Text("= ${item.result}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFFF9F0A))
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteHistoryItem(item.id) },
                                        modifier = Modifier.testTag("delete_item_${item.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete computational mark", tint = Color(0xFFFF453A))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Regular visual display screen panel calculations (takes all remaining top space)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.Black)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showHistoryPanel = true },
                        modifier = Modifier.testTag("view_history_button")
                    ) {
                        Icon(Icons.Default.History, contentDescription = "Look at memory history list", tint = Color.White)
                    }
                    Text(
                        text = historyText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8E8E93),
                        textAlign = TextAlign.Right,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (expr.isEmpty()) {
                        Text(
                            text = "0",
                            style = TextStyle(
                                color = Color(0xFF8E8E93).copy(alpha = 0.5f),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Right
                            )
                        )
                    }
                    BasicTextField(
                        value = expr,
                        onValueChange = { newValue ->
                            viewModel.setGeneralExpression(newValue)
                        },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Right
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.pressGeneralKey("=") }
                        ),
                        cursorBrush = SolidColor(Color(0xFFFF9F0A)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("general_calc_type_input")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = result,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right
                    ),
                    modifier = Modifier.testTag("general_calc_result_text"),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Standard calculator circular buttons matching screenshot
            val padButtonRows = listOf(
                listOf("AC", "%", "DEL", "/"),
                listOf("7", "8", "9", "*"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("00", "0", ".", "=")
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                for (row in padButtonRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        for (key in row) {
                            val containerCol = if (key == "=") Color(0xFFFF9F0A) else Color(0xFF262626)
                            val textCol = Color.White

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .background(color = containerCol, shape = CircleShape)
                                    .clickable { viewModel.pressGeneralKey(key) }
                                    .testTag("keypad_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (key == "DEL") {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = textCol,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    val buttonLabel = when (key) {
                                        "/" -> "÷"
                                        "*" -> "×"
                                        else -> key
                                    }
                                    Text(
                                        text = buttonLabel,
                                        color = textCol,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Medium
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

// 2. CURRENCY CONVERTER VIEW
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterView(viewModel: CalculatorViewModel) {
    val amount by viewModel.currencyAmount.collectAsStateWithLifecycle()
    val fromCurr by viewModel.currencyFrom.collectAsStateWithLifecycle()
    val toCurr by viewModel.currencyTo.collectAsStateWithLifecycle()
    val res by viewModel.currencyResult.collectAsStateWithLifecycle()

    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    val currencyOptions = viewModel.currencyRates.keys.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Static Currency Exchange Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = amount,
                    onValueChange = { viewModel.setCurrencyAmount(it) },
                    label = { Text("Amount value") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("curr_amt_input"),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = showFromMenu,
                            onExpandedChange = { showFromMenu = it }
                        ) {
                            TextField(
                                value = fromCurr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("From Currency") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showFromMenu) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("curr_from_anchor")
                            )
                            ExposedDropdownMenu(
                                expanded = showFromMenu,
                                onDismissRequest = { showFromMenu = false }
                            ) {
                                currencyOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            viewModel.setCurrencyFrom(opt)
                                            showFromMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = showToMenu,
                            onExpandedChange = { showToMenu = it }
                        ) {
                            TextField(
                                value = toCurr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("To Currency") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showToMenu) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("curr_to_anchor")
                            )
                            ExposedDropdownMenu(
                                expanded = showToMenu,
                                onDismissRequest = { showToMenu = false }
                            ) {
                                currencyOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            viewModel.setCurrencyTo(opt)
                                            showToMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.convertCurrency() },
                    modifier = Modifier.fillMaxWidth().testTag("curr_convert_btn")
                ) {
                    Text("Convert rates", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 3. UNIT CONVERTER VIEW
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterView(viewModel: CalculatorViewModel) {
    val prop by viewModel.unitProperty.collectAsStateWithLifecycle()
    val value by viewModel.unitValue.collectAsStateWithLifecycle()
    val uFrom by viewModel.unitFrom.collectAsStateWithLifecycle()
    val uTo by viewModel.unitTo.collectAsStateWithLifecycle()
    val res by viewModel.unitResult.collectAsStateWithLifecycle()

    var showPropMenu by remember { mutableStateOf(false) }
    var showFromMenu by remember { mutableStateOf(false) }
    var showToMenu by remember { mutableStateOf(false) }

    val properties = listOf("Length", "Weight", "Temperature")
    val unitsForProp = when (prop) {
        "Length" -> listOf("Meter", "Kilometer", "Mile", "Foot", "Inch", "Centimeter")
        "Weight" -> listOf("Kilogram", "Gram", "Pound", "Ounce")
        "Temperature" -> listOf("Celsius", "Fahrenheit", "Kelvin")
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Standard Metric & Imperial Unit Converter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = showPropMenu,
                        onExpandedChange = { showPropMenu = it }
                    ) {
                        TextField(
                            value = prop,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Property Choice") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showPropMenu) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = Modifier.menuAnchor().fillMaxWidth().testTag("unit_prop_anchor")
                        )
                        ExposedDropdownMenu(
                            expanded = showPropMenu,
                            onDismissRequest = { showPropMenu = false }
                        ) {
                            properties.forEach { pt ->
                                DropdownMenuItem(
                                    text = { Text(pt) },
                                    onClick = {
                                        viewModel.setUnitProperty(pt)
                                        showPropMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                TextField(
                    value = value,
                    onValueChange = { viewModel.setUnitValue(it) },
                    label = { Text("Value to convert") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("unit_val_input"),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = showFromMenu,
                            onExpandedChange = { showFromMenu = it }
                        ) {
                            TextField(
                                value = uFrom,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("From unit") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showFromMenu) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("unit_from_anchor")
                            )
                            ExposedDropdownMenu(
                                expanded = showFromMenu,
                                onDismissRequest = { showFromMenu = false }
                            ) {
                                unitsForProp.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            viewModel.setUnitFrom(unit)
                                            showFromMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = showToMenu,
                            onExpandedChange = { showToMenu = it }
                        ) {
                            TextField(
                                value = uTo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("To unit") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showToMenu) },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth().testTag("unit_to_anchor")
                            )
                            ExposedDropdownMenu(
                                expanded = showToMenu,
                                onDismissRequest = { showToMenu = false }
                            ) {
                                unitsForProp.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text(unit) },
                                        onClick = {
                                            viewModel.setUnitTo(unit)
                                            showToMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = { viewModel.convertUnits() },
                    modifier = Modifier.fillMaxWidth().testTag("unit_convert_btn")
                ) {
                    Text("Convert unit metrics", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 4. DISCOUNT CALCULATOR VIEW
@Composable
fun DiscountCalculatorView(viewModel: CalculatorViewModel) {
    val price by viewModel.discountPrice.collectAsStateWithLifecycle()
    val percent by viewModel.discountPercentage.collectAsStateWithLifecycle()
    val res by viewModel.discountResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Interactive Discount Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = price,
                    onValueChange = { viewModel.setDiscountPrice(it) },
                    label = { Text("Original price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("disc_price_input")
                )

                TextField(
                    value = percent,
                    onValueChange = { viewModel.setDiscountPercentage(it) },
                    label = { Text("Discount percent (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("disc_pct_input")
                )

                Button(
                    onClick = { viewModel.calculateDiscount() },
                    modifier = Modifier.fillMaxWidth().testTag("disc_calculate_btn")
                ) {
                    Text("Calculate savings", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 5. TIP CALCULATOR VIEW
@Composable
fun TipCalculatorView(viewModel: CalculatorViewModel) {
    val bill by viewModel.tipBill.collectAsStateWithLifecycle()
    val pct by viewModel.tipPercentage.collectAsStateWithLifecycle()
    val split by viewModel.tipSplit.collectAsStateWithLifecycle()
    val res by viewModel.tipResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Tip SPLIT sharing estimator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = bill,
                    onValueChange = { viewModel.setTipBill(it) },
                    label = { Text("Total Bill Amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("tip_bill_input")
                )

                TextField(
                    value = pct,
                    onValueChange = { viewModel.setTipPercentage(it) },
                    label = { Text("Tip percent (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("tip_pct_input")
                )

                TextField(
                    value = split,
                    onValueChange = { viewModel.setTipSplit(it) },
                    label = { Text("Number of splitting people") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("tip_split_input")
                )

                Button(
                    onClick = { viewModel.calculateTip() },
                    modifier = Modifier.fillMaxWidth().testTag("tip_calculate_btn")
                ) {
                    Text("Estimate split balances", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 6. DATE CALCULATOR VIEW
@Composable
fun DateCalculatorView(viewModel: CalculatorViewModel) {
    val start by viewModel.dateStart.collectAsStateWithLifecycle()
    val end by viewModel.dateEnd.collectAsStateWithLifecycle()
    val res by viewModel.dateResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Days & Weeks Date Interval Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = start,
                    onValueChange = { viewModel.setDateStart(it) },
                    label = { Text("Start Date (e.g. 2026-05-26)") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth().testTag("date_start_input")
                )

                TextField(
                    value = end,
                    onValueChange = { viewModel.setDateEnd(it) },
                    label = { Text("End Date (e.g. 2026-06-12)") },
                    placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth().testTag("date_end_input")
                )

                Button(
                    onClick = { viewModel.calculateDateDiff() },
                    modifier = Modifier.fillMaxWidth().testTag("date_calc_btn")
                ) {
                    Text("Identify dates difference", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 7. FUEL COST CALCULATOR VIEW
@Composable
fun FuelCostCalculatorView(viewModel: CalculatorViewModel) {
    val distance by viewModel.fcDistance.collectAsStateWithLifecycle()
    val price by viewModel.fcPrice.collectAsStateWithLifecycle()
    val mileage by viewModel.fcMileage.collectAsStateWithLifecycle()
    val res by viewModel.fcResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Travel Fuel Expenses Estimator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = distance,
                    onValueChange = { viewModel.setFcDistance(it) },
                    label = { Text("Distance (KM or Miles)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("fc_dist_input")
                )

                TextField(
                    value = price,
                    onValueChange = { viewModel.setFcPrice(it) },
                    label = { Text("Fuel price per liter/gallon ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("fc_price_input")
                )

                TextField(
                    value = mileage,
                    onValueChange = { viewModel.setFcMileage(it) },
                    label = { Text("Vehicle economy mileage (units/vol)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("fc_mil_input")
                )

                Button(
                    onClick = { viewModel.calculateFuelCost() },
                    modifier = Modifier.fillMaxWidth().testTag("fc_calculate_btn")
                ) {
                    Text("Estimate voyage expense", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 8. FUEL EFFICIENCY VIEW
@Composable
fun FuelEfficiencyCalculatorView(viewModel: CalculatorViewModel) {
    val dist by viewModel.feDistance.collectAsStateWithLifecycle()
    val fuel by viewModel.feFuel.collectAsStateWithLifecycle()
    val res by viewModel.feResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Fuel Economy Efficiency Meter", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = dist,
                    onValueChange = { viewModel.setFeDistance(it) },
                    label = { Text("Traveled distance standard") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("fe_dist_input")
                )

                TextField(
                    value = fuel,
                    onValueChange = { viewModel.setFeFuel(it) },
                    label = { Text("Fuel volume fluid liters/gallons") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("fe_fuel_input")
                )

                Button(
                    onClick = { viewModel.calculateFuelEfficiency() },
                    modifier = Modifier.fillMaxWidth().testTag("fe_calculate_btn")
                ) {
                    Text("Verify Efficiency", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 9. GPA CALCULATOR VIEW
@Composable
fun GpaCalculatorView(viewModel: CalculatorViewModel) {
    val courses = viewModel.gpaCourses
    val res by viewModel.gpaResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Semester GPA calculation matrix", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Render rows scrollably inside this card segment
                Box(modifier = Modifier.weight(1f, fill = false).heightIn(max = 280.dp)) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(courses.size) { index ->
                            if (index < courses.size) {
                                val course = courses[index]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextField(
                                        value = course.points,
                                        onValueChange = { p ->
                                            viewModel.updateGpaRow(index, p, course.credits)
                                        },
                                        label = { Text("Grade (e.g. 9)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f).testTag("gpa_pts_$index"),
                                        singleLine = true
                                    )

                                    TextField(
                                        value = course.credits,
                                        onValueChange = { c ->
                                            viewModel.updateGpaRow(index, course.points, c)
                                        },
                                        label = { Text("Credits (e.g. 4)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f).testTag("gpa_crd_$index"),
                                        singleLine = true
                                    )

                                    IconButton(
                                        onClick = { viewModel.removeGpaRow(index) },
                                        enabled = courses.size > 1,
                                        modifier = Modifier.testTag("gpa_remove_$index")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove grade row", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { viewModel.addGpaRow() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        modifier = Modifier.weight(1f).testTag("gpa_add_row_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Course")
                    }

                    Button(
                        onClick = { viewModel.calculateGPA() },
                        modifier = Modifier.weight(1f).testTag("gpa_calculate_btn")
                    ) {
                        Text("Get GPA")
                    }
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 10. HEALTH CALCULATOR (BMI) VIEW
@Composable
fun BmiCalculatorView(viewModel: CalculatorViewModel) {
    val weight by viewModel.bmiWeight.collectAsStateWithLifecycle()
    val height by viewModel.bmiHeight.collectAsStateWithLifecycle()
    val res by viewModel.bmiResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Body Mass Index Health Checker", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = weight,
                    onValueChange = { viewModel.setBmiWeight(it) },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("bmi_weight_input")
                )

                TextField(
                    value = height,
                    onValueChange = { viewModel.setBmiHeight(it) },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("bmi_height_input")
                )

                Button(
                    onClick = { viewModel.calculateBMI() },
                    modifier = Modifier.fillMaxWidth().testTag("bmi_calculate_btn")
                ) {
                    Text("Verify BMI", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 11. HEXADECIMAL CALCULATOR VIEW
@Composable
fun HexCalculatorView(viewModel: CalculatorViewModel) {
    val decVal by viewModel.hexDec.collectAsStateWithLifecycle()
    val hexVal by viewModel.hexHex.collectAsStateWithLifecycle()
    val res by viewModel.hexResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Bilateral Hexadecimal conversion standard", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = decVal,
                    onValueChange = { viewModel.updateHexFromDec(it) },
                    label = { Text("Decimal input base-10") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("hex_dec_input")
                )

                TextField(
                    value = hexVal,
                    onValueChange = { viewModel.updateDecFromHex(it) },
                    label = { Text("Hexadecimal input base-16") },
                    modifier = Modifier.fillMaxWidth().testTag("hex_hex_input")
                )
            }
        }

        ResultOutputBox(res)
    }
}


// 12. LOAN CALCULATOR VIEW
@Composable
fun LoanCalculatorView(viewModel: CalculatorViewModel) {
    val principal by viewModel.loanPrincipal.collectAsStateWithLifecycle()
    val rate by viewModel.loanRate.collectAsStateWithLifecycle()
    val months by viewModel.loanMonths.collectAsStateWithLifecycle()
    val res by viewModel.loanResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Amortized Monthly installment (EMI) Loan planner", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = principal,
                    onValueChange = { viewModel.setLoanPrincipal(it) },
                    label = { Text("Principal loan amount ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("loan_principal_input")
                )

                TextField(
                    value = rate,
                    onValueChange = { viewModel.setLoanRate(it) },
                    label = { Text("Interest rate percentage per annum (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("loan_rate_input")
                )

                TextField(
                    value = months,
                    onValueChange = { viewModel.setLoanMonths(it) },
                    label = { Text("Tenure duration (Months)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("loan_months_input")
                )

                Button(
                    onClick = { viewModel.calculateEMI() },
                    modifier = Modifier.fillMaxWidth().testTag("loan_calculate_btn")
                ) {
                    Text("Estimate EMI installments", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// 13. SALES TAX CALCULATOR VIEW
@Composable
fun SalesTaxCalculatorView(viewModel: CalculatorViewModel) {
    val price by viewModel.taxPrice.collectAsStateWithLifecycle()
    val rate by viewModel.taxRate.collectAsStateWithLifecycle()
    val res by viewModel.taxResult.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Base Sales tax and Gross calculations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TextField(
                    value = price,
                    onValueChange = { viewModel.setTaxPrice(it) },
                    label = { Text("Net base price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("tax_price_input")
                )

                TextField(
                    value = rate,
                    onValueChange = { viewModel.setTaxRate(it) },
                    label = { Text("Tax rate percentage (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("tax_rate_input")
                )

                Button(
                    onClick = { viewModel.calculateSalesTax() },
                    modifier = Modifier.fillMaxWidth().testTag("tax_calculate_btn")
                ) {
                    Text("Verify sales tax", fontWeight = FontWeight.Bold)
                }
            }
        }

        ResultOutputBox(res)
    }
}


// Helper UI component for outputs
@Composable
fun ResultOutputBox(resultText: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("result_text")
            )
        }
    }
}


data class NavigationItem(
    val tab: CalculatorTab,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val description: String
)

// Helper: Custom implementation of state flows for Compose
fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)
