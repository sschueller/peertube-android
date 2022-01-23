package net.schueller.peertube.feature_server_address.presentation.address_add_edit.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@ExperimentalComposeUiApi
@Composable
fun AddServerInputTextField(
    text: String,
    hint: String,
    visualTransformation: VisualTransformation,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onFocusChange: (FocusState) -> Unit,
    autofillTypes: List<AutofillType>,
    onFill: ((String)) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = text,
            keyboardOptions = keyboardOptions,
            onValueChange = onValueChange,
            singleLine = true,
            label = {
                Text(text = hint)
            },
            visualTransformation = visualTransformation,
            modifier = Modifier
                .fillMaxWidth()
                .autofill(
                    autofillTypes = autofillTypes,
                    onFill = onFill
                )
                .onFocusChanged {
                    onFocusChange(it)
                }
        )

    }
}

@ExperimentalComposeUiApi
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String)) -> Unit
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(
        onFill = onFill,
        autofillTypes = autofillTypes
    )
    LocalAutofillTree.current += autofillNode

    this.onGloballyPositioned {
        autofillNode.boundingBox = it.boundsInParent()
    }.onFocusChanged { focusState ->
        autofill?.run {
            if (focusState.isFocused) {
                requestAutofillForNode((autofillNode))
            } else {
                cancelAutofillForNode(autofillNode)
            }
        }
    }
}