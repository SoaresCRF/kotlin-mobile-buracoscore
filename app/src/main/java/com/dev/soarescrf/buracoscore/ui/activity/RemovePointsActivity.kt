package com.dev.soarescrf.buracoscore.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
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
 * Activity responsável por subtrair pontos com base nas cartas removidas por um jogador.
 *
 * Esta tela exibe as cartas removidas, permite ajustar suas quantidades e calcula a
 * pontuação final considerando os pontos previamente adicionados.
 */
class RemovePointsActivity : AppCompatActivity() {

    /**
     * Instância do binding da View para acesso aos componentes da interface.
     */
    private lateinit var binding: CalculateScoreBinding

    /**
     * Auxilia no acesso aos componentes da UI de pontuação.
     * */
    private lateinit var scoreViewHelper: ScoreViewHelper

    /**
     * Nome do jogador atual.
     */
    private lateinit var player: String

    /**
     * Posição do jogador na lista ou partida.
     */
    private lateinit var playerPosition: String

    /**
     * Total de pontos adicionados durante a partida.
     */
    private var totalAddPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = CalculateScoreBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        applyWindowInsets()

        // Altera os ícones da status bar para escuros
        window.setLightStatusBarIcons(true)

        scoreViewHelper = ScoreViewHelper(binding)

        initViews()
        loadIntentExtras()
        setupUI()
        setupListeners()
    }

    /**
     * Aplica os insets de sistema (como status bar) ao layout principal.
     */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Inicializa as views escondendo elementos desnecessários
     * e configurando descrições de acessibilidade.
     */
    private fun initViews() = with(binding) {
        switchSecondHand.visibility = View.GONE
        switchGoingOut.visibility = View.GONE
        scoreViewHelper.canastaLinearLayoutList.forEach { it.visibility = View.GONE }
        scoreViewHelper.cardTextViewList.forEach {
            it.contentDescription = getString(R.string.accessibility_no_cards)
        }
    }

    /**
     * Carrega dados recebidos via `Intent`, como nome do jogador,
     * posição e pontos previamente somados.
     */
    private fun loadIntentExtras() {
        intent?.let {
            player = it.getStringExtra("player").orEmpty()
            playerPosition = it.getStringExtra("player_position").orEmpty()
            totalAddPoints = it.getStringExtra("totalAddPoints")?.toIntOrNull() ?: 0
        }
        binding.textPlayer.text = player
    }

    /**
     * Configura o texto e cores dos elementos da UI para indicar
     * que se trata da ação de remover pontos.
     */
    private fun setupUI() = with(binding) {
        textAction.apply {
            text = getString(R.string.remove_points_action_title)
            setTextColor(
                ContextCompat.getColor(
                    this@RemovePointsActivity,
                    R.color.text_action_remove
                )
            )
        }
        buttonAction.text = getString(R.string.calculate_score_button)
        buttonAction.contentDescription = getString(R.string.accessibility_calculate_score_button)
    }

    /**
     * Define os listeners dos botões de adição/remoção de cartas
     * e do botão de ação final.
     */
    private fun setupListeners() = with(binding) {
        imageInfo.setOnClickListener {
            startActivity(Intent(this@RemovePointsActivity, ScoreInfoActivity::class.java))
        }

        scoreViewHelper.cardOnlyTypes.forEachIndexed { index, cardType ->
            val targetTextView = scoreViewHelper.cardTextViewList[index]

            scoreViewHelper.addCardImageViewList[index].setOnClickListener {
                updateCardCount(targetTextView, 1, cardType.maxLimit)
            }

            scoreViewHelper.removeCardImageViewList[index].setOnClickListener {
                updateCardCount(targetTextView, -1, cardType.maxLimit)
            }
        }

        buttonAction.setOnClickListener { returnFinalScore() }
    }

    /**
     * Atualiza a quantidade de cartas exibida em um `TextView`,
     * garantindo que o valor fique dentro dos limites permitidos
     * e que a descrição de acessibilidade seja atualizada adequadamente
     * com base na nova quantidade.
     *
     * @param textView O campo que exibe a quantidade.
     * @param delta A variação (positiva ou negativa) a aplicar.
     * @param maxLimit O valor máximo permitido para esse tipo de carta.
     */
    private fun updateCardCount(textView: TextView, delta: Int, maxLimit: Int) {
        val current = textView.text.toString().toIntOrNull() ?: 0
        val newTotal = (current + delta).coerceIn(0, maxLimit)
        textView.text = newTotal.toString()
        textView.contentDescription = if (newTotal == 0) {
            getString(R.string.accessibility_no_cards)
        } else {
            resources.getQuantityString(R.plurals.accessibility_total_cards, newTotal, newTotal)
        }
    }

    /**
     * Calcula a pontuação total a ser removida com base na quantidade
     * de cartas selecionadas e seus respectivos valores em pontos.
     *
     * @return A soma dos pontos a serem subtraídos.
     */
    private fun calculateRemovePoints(): Int =
        scoreViewHelper.cardOnlyTypes.mapIndexed { index, type ->
            (scoreViewHelper.cardTextViewList.getOrNull(index)?.text.toString().toIntOrNull() ?: 0) * type.points
        }.sum()

    /**
     * Calcula a pontuação final (somada - removida), retorna para a activity anterior
     * e finaliza esta activity.
     */
    private fun returnFinalScore() {
        val finalScore = totalAddPoints - calculateRemovePoints()
        setResult(RESULT_OK, Intent().apply {
            putExtra("finalScore", finalScore.toString())
            putExtra("player_position", playerPosition)
        })
        finish()
    }
}
