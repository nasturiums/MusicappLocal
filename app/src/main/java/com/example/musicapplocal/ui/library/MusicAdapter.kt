package com.example.musicapplocal.ui.library

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapplocal.databinding.ItemMusicBinding
import com.example.musicapplocal.data.model.Music


class MusicAdapter(
    private val onItemClick: (Music) -> Unit
) : RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    private val listMusics = mutableListOf<Music>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onItemClick
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listMusics[position])
    }

    override fun getItemCount() = listMusics.size

    fun updateListMusic(musics: List<Music>) {
        listMusics.apply {
            clear()
            addAll(musics)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemMusicBinding,
        onItemClick: (Music) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private var itemMusic: Music? = null

        init {
            binding.root.setOnClickListener {
                itemMusic?.let { onItemClick(it) }
            }
        }

        fun bindData(item: Music) {
            itemMusic = item
            binding.apply {
                textItemMusicName.text = item.name
                textItemMusicAuthor.text = item.author
            }
        }
    }

}
