package fi.anttonen.villematti.apps.gymbuddy.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymEntry
import kotlinx.android.synthetic.main.activity_main.*
import fi.anttonen.villematti.apps.gymbuddy.control.GymEntriesRecyclerAdapter
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.model.WeightEntry

class MainActivity : AppCompatActivity(), GymEntriesRecyclerAdapter.OnItemClickListener {

    companion object {
        private const val UPDATE_REQUEST = 1
    }

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: GymEntriesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutManager = LinearLayoutManager(this)
        gymEntriesRecyclerView.layoutManager = linearLayoutManager

        adapter = GymEntriesRecyclerAdapter()
        adapter.itemClickListener = this
        gymEntriesRecyclerView.adapter = adapter

        supportActionBar?.title = getString(R.string.main_activity_title)
    }


    /**
     * Item click listener for gym entry recycler view
     */
    override fun onItemClick(view: View, position: Int, entry: GymEntry) {
        if (entry is WeightEntry) {
            val weightDetailIntent = Intent(this, WeightEntryDetail::class.java).apply {
                putExtra(WeightEntryDetail.ENTRY_ID_KEY, entry.getEntryId())
            }

            //val weightTextPair = Pair.create(view.weight_text as View, "tWeightText")
            //val weightUnitTextPair = Pair.create(view.weight_unit_text as View, "tWeightUnitText")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity)//,
            //        weightTextPair, weightUnitTextPair)

            ActivityCompat.startActivityForResult(this@MainActivity, weightDetailIntent, UPDATE_REQUEST, options.toBundle())
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val changed = data.getBooleanExtra(WeightEntryDetail.CHANGED_KEY, false)
                val deleted = data.getBooleanExtra(WeightEntryDetail.DELETED_KEY, false)
                if (changed || deleted) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }
}
