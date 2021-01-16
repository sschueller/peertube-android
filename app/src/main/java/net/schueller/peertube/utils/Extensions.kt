package net.schueller.peertube.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.visibleIf(predicate: () -> Boolean) {
    when (predicate.invoke()) {
        true -> visible()
        else -> gone()
    }
}

fun View.goneIf(predicate: () -> Boolean) {
    when (predicate.invoke()) {
        true -> gone()
        else -> visible()
    }
}

fun View.invisibleIf(predicate: () -> Boolean) {
    when (predicate.invoke()) {
        true -> invisible()
        else -> visible()
    }
}

fun Fragment.hideKeyboard(): Boolean {
    activity?.currentFocus?.let {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
        return true
    }
    return false
}

fun AppCompatActivity.hideKeyboard(): Boolean {
    currentFocus?.let {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(it.windowToken, 0)
        return true
    }
    return false
}
