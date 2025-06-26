package com.dev.soarescrf.buracoscore.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.soarescrf.buracoscore.databinding.ActivityRulesBinding
import com.dev.soarescrf.buracoscore.utils.TextUtils
import com.dev.soarescrf.buracoscore.utils.setLightStatusBarIcons

/**
 * Activity responsável por exibir as regras do jogo de Buraco.
 *
 * As regras são mostradas de forma justificada em um container LinearLayout (ID: `rulesContainer`),
 * contendo vários TextViews.
 */
class RulesActivity : AppCompatActivity() {
    /** Binding gerado para acessar as views do layout */
    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRulesBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        applyWindowInsets()

        // Altera os ícones da status bar para escuros
        window.setLightStatusBarIcons(true)

        justifyAllTextViewsInContainer()
    }

    /**
     * Aplica os insets de sistema (status bar, navigation bar) nos componentes da interface,
     * adicionando padding conforme necessário para evitar sobreposição.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Recupera todos os [TextView]s dentro do container `rulesContainer`
     * e aplica alinhamento de texto justificado em cada um.
     */
    private fun justifyAllTextViewsInContainer() {
        binding.rulesContainer.getAllChildTextViews().forEach { textView ->
            TextUtils.applyJustifyAlignment(textView)
        }
    }

    /**
     * Recupera recursivamente todos os TextViews filhos dentro de um [ViewGroup],
     * incluindo TextViews aninhados dentro de outros layouts.
     *
     * @return Lista contendo todas as instâncias de [TextView] encontradas no container.
     */
    private fun ViewGroup.getAllChildTextViews(): List<TextView> {
        val textViewList = mutableListOf<TextView>()
        for (i in 0 until childCount) {
            when (val child = getChildAt(i)) {
                is TextView -> textViewList.add(child)
                is ViewGroup -> textViewList.addAll(child.getAllChildTextViews())
            }
        }
        return textViewList
    }
}
