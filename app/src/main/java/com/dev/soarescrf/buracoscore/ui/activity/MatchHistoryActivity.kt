package com.dev.soarescrf.buracoscore.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dev.soarescrf.buracoscore.R
import com.dev.soarescrf.buracoscore.data.database.AppDatabase
import com.dev.soarescrf.buracoscore.data.model.Match
import com.dev.soarescrf.buracoscore.databinding.ActivityMatchHistoryBinding
import com.dev.soarescrf.buracoscore.databinding.CustomDialogConfirmationBinding
import com.dev.soarescrf.buracoscore.ui.adapter.MatchAdapter
import com.dev.soarescrf.buracoscore.utils.TextUtils
import com.dev.soarescrf.buracoscore.utils.setLightStatusBarIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity responsável por exibir o histórico de partidas salvas no banco de dados.
 *
 * Esta tela permite ao usuário visualizar todas as partidas jogadas, além de oferecer
 * a opção de excluir todo o histórico de partidas salvas.
 */
class MatchHistoryActivity : AppCompatActivity() {
    /** Binding gerado para acessar as views do layout */
    private lateinit var binding: ActivityMatchHistoryBinding

    private lateinit var matchAdapter: MatchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMatchHistoryBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(binding.root)
        applyWindowInsets()

        // Altera os ícones da status bar para escuros
        window.setLightStatusBarIcons(true)

        setupRecyclerView()
        loadAndDisplayMatches()
        setupDeleteAllButton()
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
     * Configura o RecyclerView com o adapter responsável por exibir as partidas.
     */
    private fun setupRecyclerView() {
        matchAdapter = MatchAdapter()
        binding.recyclerViewMatch.adapter = matchAdapter
    }

    /**
     * Carrega a lista de partidas do banco de dados e atualiza a UI.
     */
    private fun loadAndDisplayMatches() {
        lifecycleScope.launch {
            val matches = fetchMatchesFromDatabase()
            updateMatchList(matches)
            updateDeleteAllVisibility(matches)
        }
    }

    /**
     * Define o listener do botão de excluir todo o histórico.
     */
    private fun setupDeleteAllButton() {
        binding.imageDeleteAll.setOnClickListener { showDeleteAllConfirmationDialog() }
    }

    /**
     * Realiza a consulta ao banco de dados para obter todas as partidas salvas.
     *
     * @return Lista de partidas existentes no banco de dados.
     */
    private suspend fun fetchMatchesFromDatabase(): List<Match> = withContext(Dispatchers.IO) {
        AppDatabase.getInstance(applicationContext).matchDao().getAllMatches()
    }

    /**
     * Atualiza a lista de partidas exibidas no RecyclerView.
     *
     * @param matches Lista de partidas a ser exibida.
     */
    private fun updateMatchList(matches: List<Match>) {
        matchAdapter.submitList(matches)

        if (matches.isEmpty()) {
            showToast(getString(R.string.no_matches_found))
        }
    }

    /**
     * Controla a visibilidade do botão de deletar todas as partidas.
     *
     * @param matches Lista de partidas para verificar se o botão deve ser exibido.
     */
    private fun updateDeleteAllVisibility(matches: List<Match>) {
        binding.imageDeleteAll.visibility = if (matches.isEmpty()) View.GONE else View.VISIBLE
    }

    /**
     * Exibe o diálogo de confirmação antes de excluir todo o histórico de partidas.
     */
    private fun showDeleteAllConfirmationDialog() {
        // Infla o layout do diálogo usando View Binding
        val dialogBinding = CustomDialogConfirmationBinding.inflate(layoutInflater)

        // Cria o AlertDialog com fundo transparente usando o root do binding
        val dialog = AlertDialog.Builder(this, R.style.TransparentDialog)
            .setView(dialogBinding.root)
            .create()

        // Configura os textos do diálogo (exemplo: título e mensagem)
        dialogBinding.textDialogTitle.text = getString(R.string.attention)

        dialogBinding.textMessage.text = getString(R.string.confirm_delete_all_message)
        TextUtils.applyJustifyAlignment(dialogBinding.textMessage)

        // Configura o clique no ícone de confirmação
        dialogBinding.imageCheck.setOnClickListener {
            deleteAllMatches()
            showToast(getString(R.string.all_matches_deleted))
            loadAndDisplayMatches()
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Realiza a exclusão de todas as partidas no banco de dados de forma assíncrona.
     */
    private fun deleteAllMatches() {
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(applicationContext).matchDao().deleteAllMatches()
        }
    }

    /**
     * Exibe uma mensagem Toast na tela.
     *
     * @param message Mensagem a ser exibida ao usuário.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
