package com.syllogismobile.custom_layout_inflater

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

class CustomLayoutInflater(
    context: Context,
    private val parent: LayoutInflater = LayoutInflater.from(context)
) : LayoutInflater(parent, context) {
    private val registeredAppliers: MutableMap<String, (View, String) -> Unit> = mutableMapOf()

    init {
        factory2 = WrapperFactory(factory2)
    }

    fun registerApplier(tag: String, applier: (view: View, value: String) -> Unit): CustomLayoutInflater {
        registeredAppliers[tag] = applier
        return this
    }

    override fun cloneInContext(newContext: Context): LayoutInflater {
        return CustomLayoutInflater(newContext, this)
    }

    override fun onCreateView(
        name: String?,
        attrs: AttributeSet?
    ): View? {
        for (prefix in androidPrefixes) {
            try {
                val view = createView(name, prefix, attrs)
                if (view != null) return view.apply { runAppliers(this, attrs) }
            } catch (e: ClassNotFoundException) { }
        }

        return super.onCreateView(name, attrs)?.apply { runAppliers(this, attrs) }
    }

    private fun runAppliers(view: View, attrs: AttributeSet?) {
        if (attrs == null) return

        for (registeredTag in registeredAppliers.keys) {
            attrs.getAttributeValue(NAMESPACE, registeredTag)?.let { value ->
                registeredAppliers[registeredTag]?.let {
                    it(view, value)
                }
            }
        }
    }

    inner class WrapperFactory(private val originalFactory: Factory2?) : Factory2 {
        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            return originalFactory?.onCreateView(name, context, attrs)?.apply { runAppliers(this, attrs) }
        }

        override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
            return originalFactory?.onCreateView(parent, name, context, attrs)?.apply { runAppliers(this, attrs) }
        }
    }

    companion object {
        private val androidPrefixes = listOf(
            "android.widget.",
            "android.webkit.",
            "android.app."
        )

        private const val NAMESPACE = "http://syllogismobile.wordpress.com/"
        fun from(context: Context): CustomLayoutInflater {
            return CustomLayoutInflater(context)
        }
    }
}