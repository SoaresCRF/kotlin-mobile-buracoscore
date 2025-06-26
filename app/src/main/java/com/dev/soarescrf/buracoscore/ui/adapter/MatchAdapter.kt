package com.dev.soarescrf.buracoscore.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dev.soarescrf.buracoscore.R
import com.dev.soarescrf.buracoscore.data.model.Match
import com.dev.soarescrf.buracoscore.databinding.ItemMatchBinding

/**
 * Adapter responsável por exibir uma lista de partidas de jogo ([Match]) em um [RecyclerView],
 * utilizando [ListAdapter] com [DiffUtil] para atualizações eficientes e animações automáticas.
 *
 * Aplica cores dinâmicas aos nomes e pontos dos jogadores com base no resultado da partida:
 * - Vencedor: verde
 * - Perdedor: vermelho
 * - Empate: cinza
 * - Dados inválidos: preto
 */
class MatchAdapter : ListAdapter<Match, MatchAdapter.MatchViewHolder>(MatchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = ItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder responsável por representar visualmente cada item da lista.
     *
     * Faz o binding dos dados de um objeto [Match] para os componentes da UI.
     */
    inner class MatchViewHolder(private val binding: ItemMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Atualiza a UI do item com os dados da partida.
         */
        fun bind(match: Match) = with(binding) {
            textPlayerA.text = match.playerA
            textPlayerB.text = match.playerB
            textPointsA.text = match.pointsA
            textPointsB.text = match.pointsB
            textGameDate.text = match.date

            val (pointsA, pointsB) = parsePoints(match.pointsA, match.pointsB)
            applyColors(pointsA, pointsB)

            // Acessibilidade
            textPlayerA.contentDescription =
                buildContentDescription(match.playerA, pointsA, pointsB)
            textPlayerB.contentDescription =
                buildContentDescription(match.playerB, pointsB, pointsA)
            textPointsA.contentDescription = root.context.getString(
                R.string.accessibility_player_score, match.playerA, pointsA ?: 0
            )
            textPointsB.contentDescription = root.context.getString(
                R.string.accessibility_player_score, match.playerB, pointsB ?: 0
            )
        }

        /**
         * Converte pontuações de String para Int, ou retorna null se não for numérico.
         */
        private fun parsePoints(a: String, b: String) =
            try {
                a.toInt() to b.toInt()
            } catch (e: NumberFormatException) {
                null to null
            }

        /**
         * Aplica cores nos nomes e pontos com base no resultado.
         */
        private fun applyColors(pointsA: Int?, pointsB: Int?) = with(binding) {
            val context = root.context
            val default = ContextCompat.getColor(context, android.R.color.black)

            val (colorA, colorB) = when {
                pointsA == null || pointsB == null -> default to default
                pointsA > pointsB -> getColorPair(R.color.win, R.color.defeat)
                pointsB > pointsA -> getColorPair(R.color.defeat, R.color.win)
                else -> getColorPair(R.color.draw, R.color.draw)
            }

            textPlayerA.setTextColor(colorA)
            textPointsA.setTextColor(colorA)
            textPlayerB.setTextColor(colorB)
            textPointsB.setTextColor(colorB)
        }

        /**
         * Retorna um par de cores com base nos recursos.
         */
        private fun getColorPair(colorARes: Int, colorBRes: Int): Pair<Int, Int> {
            val context = binding.root.context
            return ContextCompat.getColor(context, colorARes) to ContextCompat.getColor(
                context,
                colorBRes
            )
        }

        /**
         * Gera a descrição de acessibilidade para o jogador.
         */
        private fun buildContentDescription(name: String, points: Int?, opponent: Int?): String {
            val context = binding.root.context
            return when {
                points == null || opponent == null ->
                    context.getString(R.string.accessibility_result_invalid)

                points > opponent ->
                    context.getString(R.string.accessibility_result_win_with_name, name)

                points < opponent ->
                    context.getString(R.string.accessibility_result_lose_with_name, name)

                else ->
                    context.getString(R.string.accessibility_result_draw)
            }
        }
    }
}

/**
 * [DiffUtil.ItemCallback] para otimizar as atualizações da lista.
 */
class MatchDiffCallback : DiffUtil.ItemCallback<Match>() {
    override fun areItemsTheSame(oldItem: Match, newItem: Match) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Match, newItem: Match) = oldItem == newItem
}
