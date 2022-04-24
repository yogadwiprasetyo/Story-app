package com.yogaprasetyo.storyapp.ui.auth.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.yogaprasetyo.storyapp.R

class PasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttribute: Int) : super(
        context,
        attributeSet,
        defStyleAttribute
    ) {
        init()
    }

    /**
     * Validation minimum character for the password input
     * */
    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                error = if (s.toString().length < MINIMUM_CHARACTER) {
                    resources.getString(R.string.input_error_password)
                } else {
                    null
                }
            }
        })

        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    companion object {
        private const val MINIMUM_CHARACTER = 6
    }
}