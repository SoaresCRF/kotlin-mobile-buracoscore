package com.dev.soarescrf.buracoscore.utils

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.dev.soarescrf.buracoscore.databinding.CalculateScoreBinding

/**
 * Classe auxiliar responsável por agrupar e organizar os componentes da interface
 * relacionados à contagem de cartas e canastras na tela de pontuação.
 *
 * Esta classe facilita o acesso e manipulação dos elementos da UI, como TextViews e ImageViews,
 * além de fornecer a lista completa de tipos de cartas utilizados na pontuação.
 *
 * @param binding Instância do ViewBinding da tela de pontuação.
 */
class ScoreViewHelper(private val binding: CalculateScoreBinding) {

    /**
     * Lista de todos os tipos de cartas e canastras utilizados para pontuação.
     */
    val allCardTypes: List<CardType> by lazy {
        CardType.cardTypesForCardsOnly() + listOf(
            CardType.CLOSED_CANASTA,
            CardType.CLEAN_CANASTA,
            CardType.ROYAL_CANASTA,
            CardType.AS_CANASTA
        )
    }

    /**
     * Lista de todos os tipos de cartas disponíveis para operações de subtração.
     */
    val cardOnlyTypes by lazy {
        CardType.cardTypesForCardsOnly()
    }

    /**
     * Lista de TextViews responsáveis por exibir a quantidade de cartas
     * de cada tipo de pontuação (5, 10, 15 e 20 pontos).
     */
    val cardTextViewList: Array<TextView> by lazy {
        with(binding) {
            arrayOf(
                textQtdCard5points,
                textQtdCard10points,
                textQtdCard15points,
                textQtdCard20points
            )
        }
    }

    /**
     * Lista de TextViews responsáveis por exibir a quantidade de canastras
     * (suja, limpa, real e de ases).
     */
    val canastaTextViewList: Array<TextView> by lazy {
        with(binding) {
            arrayOf(
                textQtdDirtyCanasta,
                textQtdCleanCanasta,
                textQtdRoyalCanasta,
                textQtdAsCanasta
            )
        }
    }

    /**
     * Lista de ImageViews que acionam a adição de cartas e canastras.
     */
    val addCardImageViewList: Array<ImageView> by lazy {
        with(binding) {
            arrayOf(
                imageAddCard5points,
                imageAddCard10points,
                imageAddCard15points,
                imageAddCard20points,
                imageAddDirtyCanasta,
                imageAddCleanCanasta,
                imageAddRoyalCanasta,
                imageAddAsCanasta
            )
        }
    }

    /**
     * Lista de ImageViews que acionam a remoção de cartas e canastras.
     */
    val removeCardImageViewList: Array<ImageView> by lazy {
        with(binding) {
            arrayOf(
                imageRemoveCard5points,
                imageRemoveCard10points,
                imageRemoveCard15points,
                imageRemoveCard20points,
                imageRemoveDirtyCanasta,
                imageRemoveCleanCanasta,
                imageRemoveRoyalCanasta,
                imageRemoveAsCanasta
            )
        }
    }

    /**
     * Lista dos layouts lineares que representam os tipos de canastra.
     */
    val canastaLinearLayoutList: Array<LinearLayout> by lazy {
        with(binding) {
            arrayOf(
                linearLayoutCleanCanasta,
                linearLayoutDirtyCanasta,
                linearLayoutRoyalCanasta,
                linearLayoutAsCanasta
            )
        }
    }
}
