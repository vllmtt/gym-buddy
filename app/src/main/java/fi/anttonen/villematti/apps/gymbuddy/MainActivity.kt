package fi.anttonen.villematti.apps.gymbuddy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import fi.anttonen.villematti.apps.gymbuddy.R.id.gymEntriesRecyclerView
import fi.anttonen.villematti.apps.gymbuddy.model.interfaces.GymEntry
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.weight_entry_row.view.*
import android.support.v4.util.Pair

class MainActivity : AppCompatActivity(), GymEntriesRecyclerAdapter.OnItemClickListener {

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

            ActivityCompat.startActivity(this@MainActivity, weightDetailIntent, options.toBundle())
        }
    }
}
