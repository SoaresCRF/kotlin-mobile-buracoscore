package com.dev.soarescrf.buracoscore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.dev.soarescrf.buracoscore.R
import com.dev.soarescrf.buracoscore.databinding.CalculateScoreBinding
import com.dev.soarescrf.buracoscore.utils.CardType
import com.dev.soarescrf.buracoscore.utils.ScoreViewHelper
import com.dev.soarescrf.buracoscore.utils.setLightStatusBarIcons

/**
 * Activity responsável por permitir ao usuário adicionar pontos com base em cartas e canastras.
 * Após somar os pontos, permite navegar para a tela de remoção de pontos.
 */
class AddPointsActivity : AppCompatActivity() {

    /**
     * Instância do binding da View para acesso aos componentes da interface.
     */
    private lateinit var binding: CalculateScoreBinding

    /**
     * Auxilia no acesso aos componentes da UI de pontuação.
     * */
    private lateinit var scoreViewHelper: ScoreViewHelper

    /**
     * Launcher utilizado para abrir a tela de remoção de pontos e aguardar o resultado.
     */
    private lateinit var removePointsLauncher: ActivityResultLauncher<Intent>

    companion object {
        /** Chave para o extra que representa o jogador no Intent. */
        private const val EXTRA_PLAYER = "player"

        /** Chave para o extra que representa a posição do jogador. */
        private const val EXTRA_PLAYER_POSITION = "player_position"

        /** Chave para o extra que representa os pontos totais adicionados. */
        private const val EXTRA_TOTAL_ADD_POINTS = "totalAddPoints"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CalculateScoreBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        applyWindowInsets()

        // Altera os ícones da status bar para escuros
        window.setLightStatusBarIcons(true)

        scoreViewHelper = ScoreViewHelper(binding)

        setupActivityResult()
        setupAccessibility()
        setupUI()
        setupListeners()
    }

    /**
     * Aplica os insets de sistema para ajustar a UI aos elementos da barra de status e navegação.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val bars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    /**
     * Inicializa o launcher para a tela de remoção de pontos.
     */
    private fun setupActivityResult() {
        removePointsLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    setResult(RESULT_OK, result.data)
                    finish()
                }
            }
    }

    /**
     * Configura elementos visuais da interface.
     */
    private fun setupUI() = with(binding) {
        textAction.apply {
            text = getString(R.string.add_points_action_title)
            setTextColor(ContextCompat.getColor(this@AddPointsActivity, R.color.text_action_add))
        }
        buttonAction.text = getString(R.string.next)
        buttonAction.contentDescription = getString(R.string.accessibility_next)
        textPlayer.text = intent.getStringExtra("player_name")
    }

    /**
     * Define listeners para os botões de adicionar/remover cartas e avançar.
     */
    private fun setupListeners() = with(binding) {
        imageInfo.setOnClickListener {
            startActivity(Intent(this@AddPointsActivity, ScoreInfoActivity::class.java))
        }

        scoreViewHelper.allCardTypes.forEachIndexed { index, cardType ->
            val targetTextView = getCorrespondingTextView(index)

            scoreViewHelper.addCardImageViewList[index].setOnClickListener {
                updateCardCount(targetTextView, cardType, 1)
            }

            scoreViewHelper.removeCardImageViewList[index].setOnClickListener {
                updateCardCount(targetTextView, cardType, -1)
            }
        }

        buttonAction.setOnClickListener { openRemovePointsActivity() }
    }

    /**
     * Atualiza a contagem exibida em um [TextView] para um determinado tipo de carta.
     *
     * Esta função incrementa ou decrementa a contagem atual do TextView com base no valor de [delta],
     * respeitando os limites definidos pelo [cardType]. Se houver mudança no valor, o TextView é atualizado
     * e sua descrição de acessibilidade é ajustada.
     *
     * @param textView O TextView que exibe a quantidade de cartas.
     * @param cardType O tipo de carta, que define o limite máximo permitido.
     * @param delta O valor a ser somado ou subtraído da contagem atual (pode ser negativo).
     */
    private fun updateCardCount(textView: TextView, cardType: CardType, delta: Int) {
        val current = textView.text.toString().trim().toIntOrNull() ?: 0
        val newTotal = (current + delta).coerceIn(0, cardType.maxLimit)
        if (newTotal != current) {
            textView.text = newTotal.toString()
            updateCardAccessibilityDescription(textView, newTotal)
        }
    }

    /**
     * Calcula o total de pontos somando as cartas, canastras e bônus (batida e segunda mão).
     *
     * @return Total de pontos calculado.
     */
    private fun calculateTotalPoints(): Int {
        val totalCardsAndCanastas = scoreViewHelper.allCardTypes.mapIndexed { index, cardType ->
            getIntFromTextView(getCorrespondingTextView(index)) * cardType.points
        }.sum()

        val bonusPoints = listOfNotNull(
            CardType.GOING_OUT.takeIf { binding.switchGoingOut.isChecked },
            CardType.SECOND_HAND.takeIf { binding.switchSecondHand.isChecked }
        ).sumOf { it.points }

        return totalCardsAndCanastas + bonusPoints
    }

    /**
     * Retorna o [TextView] correspondente ao índice fornecido.
     */
    private fun getCorrespondingTextView(index: Int): TextView =
        if (index < scoreViewHelper.cardTextViewList.size) scoreViewHelper.cardTextViewList[index]
        else scoreViewHelper.canastaTextViewList[index - scoreViewHelper.cardTextViewList.size]

    /**
     * Converte o texto de um [TextView] em número inteiro.
     */
    private fun getIntFromTextView(textView: TextView): Int =
        textView.text.toString().trim().toIntOrNull() ?: 0

    /**
     * Define descrições de acessibilidade iniciais para cartas e canastras.
     */
    private fun setupAccessibility() {
        scoreViewHelper.cardTextViewList.forEach {
            it.contentDescription = getString(R.string.accessibility_no_cards)
        }

        val canastaDescriptions = listOf(
            R.string.accessibility_no_dirty_canasta,
            R.string.accessibility_no_clean_canasta,
            R.string.accessibility_no_royal_canasta,
            R.string.accessibility_no_as_canasta
        )

        scoreViewHelper.canastaTextViewList.forEachIndexed { index, textView ->
            textView.contentDescription = getString(canastaDescriptions[index])
        }
    }

    /**
     * Atualiza a descrição de acessibilidade de um [TextView] com base na quantidade de cartas.
     *
     * @param textView O campo a ser atualizado.
     * @param total Quantidade total.
     */
    private fun updateCardAccessibilityDescription(textView: TextView, total: Int) {
        textView.contentDescription = when (textView) {
            in scoreViewHelper.cardTextViewList -> if (total == 0)
                getString(R.string.accessibility_no_cards)
            else
                resources.getQuantityString(R.plurals.accessibility_total_cards, total, total)

            scoreViewHelper.canastaTextViewList[0] -> getPluralOrNone(
                total,
                R.string.accessibility_no_dirty_canasta,
                R.plurals.accessibility_total_dirty_canasta
            )

            scoreViewHelper.canastaTextViewList[1] -> getPluralOrNone(
                total,
                R.string.accessibility_no_clean_canasta,
                R.plurals.accessibility_total_clean_canasta
            )

            scoreViewHelper.canastaTextViewList[2] -> getPluralOrNone(
                total,
                R.string.accessibility_no_royal_canasta,
                R.plurals.accessibility_total_royal_canasta
            )

            scoreViewHelper.canastaTextViewList[3] -> getPluralOrNone(
                total,
                R.string.accessibility_no_as_canasta,
                R.plurals.accessibility_total_as_canasta
            )

            else -> getString(R.string.accessibility_no_cards)
        }
    }

    /**
     * Retorna a string de acessibilidade apropriada com base na quantidade.
     */
    private fun getPluralOrNone(total: Int, noneRes: Int, pluralRes: Int): String =
        if (total == 0) getString(noneRes)
        else resources.getQuantityString(pluralRes, total, total)

    /**
     * Abre a tela de remoção de pontos, passando os dados atuais via intent.
     */
    private fun openRemovePointsActivity() {
        val playerPosition = intent.getStringExtra(EXTRA_PLAYER_POSITION)
        val intent = Intent(this, RemovePointsActivity::class.java).apply {
            putExtra(EXTRA_TOTAL_ADD_POINTS, calculateTotalPoints().toString())
            putExtra(EXTRA_PLAYER, binding.textPlayer.text.toString())
            putExtra(EXTRA_PLAYER_POSITION, playerPosition)
        }
        removePointsLauncher.launch(intent)
    }
}
