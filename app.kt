Okay, here's a complete Android Kotlin project for a basic calculator.

This will provide:
1.  **`MainActivity.kt`**: The Kotlin code for the app's logic.
2.  **`activity_main.xml`**: The layout XML for the user interface.
3.  **`build.gradle (app)`**: The module-level Gradle file (essential dependencies).
4.  **`strings.xml`**: For app name and other strings.
5.  **`AndroidManifest.xml`**: The application manifest.

---

**1. `MainActivity.kt` (app/src/main/java/com/example/calculatorapp/MainActivity.kt)**

```kotlin
package com.example.calculatorapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentNumber: String = ""
    private var lastNumber: Double = 0.0
    private var currentOperator: String? = null
    private var isNewOperation: Boolean = true // To clear display for new number after operator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNumberButtons()
        setupOperatorButtons()
        setupSpecialButtons()
    }

    private fun setupNumberButtons() {
        val numberButtons = listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )

        numberButtons.forEach { button ->
            button.setOnClickListener { onNumberClick(it) }
        }
    }

    private fun setupOperatorButtons() {
        val operatorButtons = listOf(
            binding.btnAdd, binding.btnSubtract, binding.btnMultiply, binding.btnDivide
        )

        operatorButtons.forEach { button ->
            button.setOnClickListener { onOperatorClick(it) }
        }
    }

    private fun setupSpecialButtons() {
        binding.btnDecimal.setOnClickListener { onDecimalClick() }
        binding.btnClear.setOnClickListener { onClearClick() }
        binding.btnEquals.setOnClickListener { onEqualsClick() }
        binding.btnPlusMinus.setOnClickListener { onPlusMinusClick() }
    }

    private fun onNumberClick(view: View) {
        val button = view as Button
        if (isNewOperation) {
            currentNumber = button.text.toString()
            isNewOperation = false
        } else {
            // Prevent multiple leading zeros unless it's "0."
            if (currentNumber == "0" && button.text.toString() == "0" && !currentNumber.contains(".")) {
                // Do nothing, keep "0"
            } else {
                currentNumber += button.text.toString()
            }
        }
        binding.tvDisplay.text = currentNumber
    }

    private fun onOperatorClick(view: View) {
        val button = view as Button
        if (currentNumber.isEmpty()) {
            // If no number entered yet, just change the operator
            currentOperator = button.text.toString()
            binding.tvDisplay.text = "0" // Or show the operator
            return
        }

        if (currentOperator != null && !isNewOperation) {
            // If there's a pending operation and a number has been entered, calculate first
            calculateResult()
        } else if (currentNumber.isNotEmpty()) {
            lastNumber = currentNumber.toDouble()
        }

        currentOperator = button.text.toString()
        isNewOperation = true // Next number entered will start a new number
        binding.tvDisplay.text = formatNumber(lastNumber) // Show the result or the last number
    }

    private fun onDecimalClick() {
        if (isNewOperation) {
            currentNumber = "0."
            isNewOperation = false
        } else if (!currentNumber.contains(".")) {
            currentNumber += "."
        }
        binding.tvDisplay.text = currentNumber
    }

    private fun onClearClick() {
        currentNumber = ""
        lastNumber = 0.0
        currentOperator = null
        isNewOperation = true
        binding.tvDisplay.text = "0"
    }

    private fun onEqualsClick() {
        if (currentOperator == null || currentNumber.isEmpty()) {
            return // Nothing to calculate
        }
        calculateResult()
        currentOperator = null // Clear operator after calculation
        isNewOperation = true // Ready for a new operation or to continue with the result
        currentNumber = formatNumber(lastNumber) // Set currentNumber to the result for further operations
    }

    private fun onPlusMinusClick() {
        if (currentNumber.isNotEmpty() && currentNumber != "0") {
            if (currentNumber.startsWith("-")) {
                currentNumber = currentNumber.substring(1)
            } else {
                currentNumber = "-$currentNumber"
            }
            binding.tvDisplay.text = currentNumber
        } else if (lastNumber != 0.0 && currentNumber.isEmpty() && currentOperator == null) {
            // If result is displayed and no new number, toggle the result
            lastNumber *= -1
            binding.tvDisplay.text = formatNumber(lastNumber)
        }
    }

    private fun calculateResult() {
        val num2 = currentNumber.toDoubleOrNull() ?: return // If currentNumber is empty, do nothing
        var result: Double = lastNumber

        when (currentOperator) {
            "+" -> result = lastNumber + num2
            "-" -> result = lastNumber - num2
            "*" -> result = lastNumber * num2
            "/" -> {
                if (num2 != 0.0) {
                    result = lastNumber / num2
                } else {
                    binding.tvDisplay.text = "Error"
                    lastNumber = 0.0
                    currentNumber = ""
                    currentOperator = null
                    isNewOperation = true
                    return
                }
            }
        }
        lastNumber = result
        binding.tvDisplay.text = formatNumber(result)
        currentNumber = "" // Clear current number after calculation
    }

    // Formats a double to an integer string if it's a whole number, otherwise keeps decimal
    private fun formatNumber(number: Double): String {
        return if (number == number.toLong().toDouble()) {
            number.toLong().toString()
        } else {
            number.toString()
        }
    }
}
```

---

**2. `activity_main.xml` (app/src/main/res/layout/activity_main.xml)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#212121"
    tools:context=".MainActivity">

    <!-- Display TextView -->
    <TextView
        android:id="@+id/tvDisplay"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="16dp"
        android:ellipsize="end"
        android:gravity="end"
        android:maxLines="1"
        android:text="0"
        android:textColor="#FFFFFF"
        android:textSize="64sp"
        app:layout_constraintBottom_toTopOf="@+id/guideline"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <!-- Guideline to separate display from buttons -->
    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_percent="0.3" />

    <!-- Button Grid -->

    <!-- Row 1: AC, +/-, %, / -->
    <Button
        android:id="@+id/btnClear"
        style="@style/CalculatorButton.Function"
        android:text="AC"
        app:layout_constraintBottom_toTopOf="@+id/btn7"
        app:layout_constraintEnd_toStartOf="@+id/btnPlusMinus"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@+id/guideline" />

    <Button
        android:id="@+id/btnPlusMinus"
        style="@style/CalculatorButton.Function"
        android:text="+/-"
        app:layout_constraintBottom_toTopOf="@+id/btn8"
        app:layout_constraintEnd_toStartOf="@+id/btnPercent"
        app:layout_constraintStart_toEndOf="@+id/btnClear"
        app:layout_constraintTop_toTopOf="@+id/guideline" />

    <Button
        android:id="@+id/btnPercent"
        style="@style/CalculatorButton.Function"
        android:text="%"
        app:layout_constraintBottom_toTopOf="@+id/btn9"
        app:layout_constraintEnd_toStartOf="@+id/btnDivide"
        app:layout_constraintStart_toEndOf="@+id/btnPlusMinus"
        app:layout_constraintTop_toTopOf="@+id/guideline"
        android:visibility="invisible" tools:visibility="visible"/> <!-- Percent not implemented in logic, hide for now -->

    <Button
        android:id="@+id/btnDivide"
        style="@style/CalculatorButton.Operator"
        android:text="/"
        app:layout_constraintBottom_toTopOf="@+id/btnMultiply"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/btnPercent"
        app:layout_constraintTop_toTopOf="@+id/guideline" />

    <!-- Row 2: 7, 8, 9, * -->
    <Button
        android:id="@+id/btn7"
        style="@style/CalculatorButton.Number"
        android:text="7"
        app:layout_constraintBottom_toTopOf="@+id/btn4"
        app:layout_constraintEnd_toStartOf="@+id/btn8"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btnClear" />

    <Button
        android:id="@+id/btn8"
        style="@style/CalculatorButton.Number"
        android:text="8"
        app:layout_constraintBottom_toTopOf="@+id/btn5"
        app:layout_constraintEnd_toStartOf="@+id/btn9"
        app:layout_constraintStart_toEndOf="@+id/btn7"
        app:layout_constraintTop_toBottomOf="@+id/btnPlusMinus" />

    <Button
        android:id="@+id/btn9"
        style="@style/CalculatorButton.Number"
        android:text="9"
        app:layout_constraintBottom_toTopOf="@+id/btn6"
        app:layout_constraintEnd_toStartOf="@+id/btnMultiply"
        app:layout_constraintStart_toEndOf="@+id/btn8"
        app:layout_constraintTop_toBottomOf="@+id/btnPercent" />

    <Button
        android:id="@+id/btnMultiply"
        style="@style/CalculatorButton.Operator"
        android:text="*"
        app:layout_constraintBottom_toTopOf="@+id/btnSubtract"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/btn9"
        app:layout_constraintTop_toBottomOf="@+id/btnDivide" />

    <!-- Row 3: 4, 5, 6, - -->
    <Button
        android:id="@+id/btn4"
        style="@style/CalculatorButton.Number"
        android:text="4"
        app:layout_constraintBottom_toTopOf="@+id/btn1"
        app:layout_constraintEnd_toStartOf="@+id/btn5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn7" />

    <Button
        android:id="@+id/btn5"
        style="@style/CalculatorButton.Number"
        android:text="5"
        app:layout_constraintBottom_toTopOf="@+id/btn2"
        app:layout_constraintEnd_toStartOf="@+id/btn6"
        app:layout_constraintStart_toEndOf="@+id/btn4"
        app:layout_constraintTop_toBottomOf="@+id/btn8" />

    <Button
        android:id="@+id/btn6"
        style="@style/CalculatorButton.Number"
        android:text="6"
        app:layout_constraintBottom_toTopOf="@+id/btn3"
        app:layout_constraintEnd_toStartOf="@+id/btnSubtract"
        app:layout_constraintStart_toEndOf="@+id/btn5"
        app:layout_constraintTop_toBottomOf="@+id/btn9" />

    <Button
        android:id="@+id/btnSubtract"
        style="@style/CalculatorButton.Operator"
        android:text="-"
        app:layout_constraintBottom_toTopOf="@+id/btnAdd"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/btn6"
        app:layout_constraintTop_toBottomOf="@+id/btnMultiply" />

    <!-- Row 4: 1, 2, 3, + -->
    <Button
        android:id="@+id/btn1"
        style="@style/CalculatorButton.Number"
        android:text="1"
        app:layout_constraintBottom_toTopOf="@+id/btn0"
        app:layout_constraintEnd_toStartOf="@+id/btn2"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn4" />

    <Button
        android:id="@+id/btn2"
        style="@style/CalculatorButton.Number"
        android:text="2"
        app:layout_constraintBottom_toTopOf="@+id/btn0"
        app:layout_constraintEnd_toStartOf="@+id/btn3"
        app:layout_constraintStart_toEndOf="@+id/btn1"
        app:layout_constraintTop_toBottomOf="@+id/btn5" />

    <Button
        android:id="@+id/btn3"
        style="@style/CalculatorButton.Number"
        android:text="3"
        app:layout_constraintBottom_toTopOf="@+id/btnDecimal"
        app:layout_constraintEnd_toStartOf="@+id/btnAdd"
        app:layout_constraintStart_toEndOf="@+id/btn2"
        app:layout_constraintTop_toBottomOf="@+id/btn6" />

    <Button
        android:id="@+id/btnAdd"
        style="@style/CalculatorButton.Operator"
        android:text="+"
        app:layout_constraintBottom_toTopOf="@+id/btnEquals"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/btn3"
        app:layout_constraintTop_toBottomOf="@+id/btnSubtract" />

    <!-- Row 5: 0, ., = -->
    <Button
        android:id="@+id/btn0"
        style="@style/CalculatorButton.Number"
        android:text="0"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/btnDecimal"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn1" />

    <Button
        android:id="@+id/btnDecimal"
        style="@style/CalculatorButton.Number"
        android:text="."
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toStartOf="@+id/btnEquals"
        app:layout_constraintStart_toEndOf="@+id/btn0"
        app:layout_constraintTop_toBottomOf="@+id/btn3" />

    <Button
        android:id="@+id/btnEquals"
        style="@style/CalculatorButton.Operator"
        android:text="="
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/btnDecimal"
        app:layout_constraintTop_toBottomOf="@+id/btnAdd" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

---

**3. `styles.xml` (app/src/main/res/values/styles.xml)**

*   **Note**: In modern Android, `styles.xml` is often `themes.xml`. If you create a new project, you'll likely find `themes.xml` and `themes.xml (night)`. You can add these styles to `themes.xml` under `<resources>`.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Base application theme. -->
    <style name="Theme.CalculatorApp" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#FF9800</item>
        <item name="colorPrimaryVariant">#F57C00</item>
        <item name="colorOnPrimary">#FFFFFF</item>
        <item name="colorSecondary">#03A9F4</item>
        <item name="colorSecondaryVariant">#0288D1</item>
        <item name="colorOnSecondary">#FFFFFF</item>
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>

    <!-- Base style for all calculator buttons -->
    <style name="CalculatorButton" parent="Widget.MaterialComponents.Button">
        <item name="android:layout_width">0dp</item>
        <item name="android:layout_height">0dp</item>
        <item name="android:textSize">32sp</item>
        <item name="android:textColor">@color/white</item>
        <item name="android:backgroundTint">#424242</item>
        <item name="android:layout_margin">4dp</item>
        <item name="cornerRadius">32dp</item>
    </style>

    <!-- Style for number buttons -->
    <style name="CalculatorButton.Number" parent="CalculatorButton">
        <item name="android:backgroundTint">#616161</item>
    </style>

    <!-- Style for operator buttons -->
    <style name="CalculatorButton.Operator" parent="CalculatorButton">
        <item name="android:backgroundTint">@color/colorPrimary</item>
    </style>

    <!-- Style for function buttons (AC, +/-, %) -->
    <style name="CalculatorButton.Function" parent="CalculatorButton">
        <item name="android:backgroundTint">#9E9E9E</item>
        <item name="android:textColor">#212121</item>
    </style>

</resources>
```

**4. `colors.xml` (app/src/main/res/values/colors.xml)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

    <!-- Primary colors for the calculator theme -->
    <color name="colorPrimary">#FF9800</color>
    <color name="colorPrimaryVariant">#F57C00</color>
    <color name="colorOnPrimary">#FFFFFF</color>
</resources>
```

---

**5. `build.gradle (app)` (app/build.gradle)**

```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.calculatorapp'
    compileSdk 34 // Or latest stable SDK

    defaultConfig {
        applicationId "com.example.calculatorapp"
        minSdk 24
        targetSdk 34 // Or latest stable SDK
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = '1.8'
    }
    buildFeatures {
        viewBinding true // Enable View Binding
    }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0' // Or latest stable version
    implementation 'androidx.appcompat:appcompat:1.6.1' // Or latest stable version
    implementation 'com.google.android.material:material:1.11.0' // Or latest stable version
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4' // Or latest stable version
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

---

**6. `AndroidManifest.xml` (app/src/main/AndroidManifest.xml)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.CalculatorApp"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:screenOrientation="portrait"> <!-- Lock to portrait for calculator -->
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

---

**7. `strings.xml` (app/src/main/res/values/strings.xml)**

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">CalculatorApp</string>
</resources>
```

---

**How to use this:**

1.  **Create a New Android Project:** Open Android Studio, select "New Project" -> "Empty Activity" -> "Next".
2.  **Configure Project:**
    *   **Name:** `CalculatorApp`
    *   **Package name:** `com.example.calculatorapp` (or your preferred)
    *   **Language:** `Kotlin`
    *   **Minimum SDK:** `API 24` (or higher if you prefer)
    *   Click "Finish".
3.  **Replace Files:**
    *   Navigate to `app/src/main/java/com/example/calculatorapp/` and replace the content of `MainActivity.kt` with the provided code.
    *   Navigate to `app/src/main/res/layout/` and replace the content of `activity_main.xml` with the provided code.
    *   Navigate to `app/src/main/res/values/` and replace `themes.xml` (or `styles.xml` if it exists) and `colors.xml` with the provided content.
    *   Navigate to `app/` and replace the content of `build.gradle` (the one for the `app` module, not the project one) with the provided code.
    *   Navigate to `app/src/main/` and replace `AndroidManifest.xml` with the provided code.
    *   Navigate to `app/src/main/res/values/` and replace `strings.xml` with the provided code.
4.  **Sync Gradle:** Android Studio will prompt you to "Sync Now" after changing `build.gradle`. Do this.
5.  **Run:** Click the "Run" button (green triangle) in Android Studio to deploy the app to an emulator or a connected device.

This calculator handles basic arithmetic operations (+, -, \*, /), decimal numbers, positive/negative toggling, and clearing the display. It also includes basic error handling for division by zero. The UI is designed with `ConstraintLayout` and uses Material Design buttons for a modern look.