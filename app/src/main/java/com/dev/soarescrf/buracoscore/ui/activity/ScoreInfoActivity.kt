package com.dev.soarescrf.buracoscore.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.soarescrf.buracoscore.databinding.ActivityScoreInfoBinding
import com.dev.soarescrf.buracoscore.utils.TextUtils
import com.dev.soarescrf.buracoscore.utils.setLightStatusBarIcons

/**
 * Activity responsável por exibir informações detalhadas de pontuação no jogo.
 *
 * Todos os textos apresentados são justificados automaticamente, garantindo
 * melhor legibilidade para o usuário.
 *
 * O layout principal utiliza o container com ID `scoreInfoContainer`.
 */
class ScoreInfoActivity : AppCompatActivity() {
    /** Binding gerado para acessar as views do layout */
    private lateinit var binding: ActivityScoreInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScoreInfoBinding.inflate(layoutInflater)
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
     * Recupera todos os [TextView]s dentro do container `scoreInfoContainer`
     * e aplica alinhamento de texto justificado em cada um.
     */
    private fun justifyAllTextViewsInContainer() {
        binding.scoreInfoContainer.getAllChildTextViews().forEach { textView ->
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
