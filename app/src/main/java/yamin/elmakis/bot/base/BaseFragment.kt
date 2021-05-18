package yamin.elmakis.bot.base

import androidx.fragment.app.Fragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein

abstract class BaseFragment : Fragment(), KodeinAware {

    private val closest by closestKodein()

    override val kodein by Kodein.lazy {
        extend(closest)
        getDependenciesModule()?.let {
            import(it)
        }
    }

    protected open fun getDependenciesModule(): Kodein.Module? {
        return null
    }
}