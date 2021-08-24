import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.ui.base.BasePresenter
import com.example.musicapplocal.ui.base.BaseView

interface MainContract {
    interface Presenter : BasePresenter {
        fun loadDataSong()
        fun loadDataNextMusic(id: String, musics: MutableList<Music>)
        fun loadDataPrevMusic(id: String, musics: MutableList<Music>)
    }

    interface View : BaseView<Presenter> {
        fun loadListMusic(listMusics: ArrayList<Music>)
        fun loadMusicFailed()
        fun loadNextMusic(music: Music)
        fun loadPrevMusic(music: Music)
    }
}
