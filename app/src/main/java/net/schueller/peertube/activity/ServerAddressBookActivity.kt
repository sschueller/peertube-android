/*
 * Copyright (C) 2020 Stefan Schüller <sschueller@techdroid.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.schueller.peertube.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import net.schueller.peertube.R
import net.schueller.peertube.adapter.ServerListAdapter
import net.schueller.peertube.database.Server
import net.schueller.peertube.database.ServerViewModel
import net.schueller.peertube.databinding.ActivityServerAddressBookBinding
import net.schueller.peertube.fragment.AddServerFragment
import java.util.*

class ServerAddressBookActivity : CommonActivity() {

    private val TAG = "ServerAddressBookActivity"

    private val mServerViewModel: ServerViewModel by viewModels()
    private var addServerFragment: AddServerFragment? = null

    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }


    private lateinit var mBinding: ActivityServerAddressBookBinding


    override fun onSupportNavigateUp(): Boolean {
        finish() // close this activity as oppose to navigating up
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityServerAddressBookBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // Setting toolbar as the ActionBar with setSupportActionBar() call
        setSupportActionBar(mBinding.toolBarServerAddressBook)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)
        }


        showServers()

        mBinding.addServer.setOnClickListener {
            Log.d(TAG, "Click")

            val fragmentTransaction = fragmentManager.beginTransaction()
            addServerFragment = AddServerFragment().also {
                fragmentTransaction.replace(R.id.server_book, it)
                fragmentTransaction.commit()
                mBinding.addServer.hide()
            }
        }
    }

    private fun showServers() {
        val adapter = ServerListAdapter(this).also {
            mBinding.serverListRecyclerview.adapter = it
        }

        // Delete items on swipe
        val helper = ItemTouchHelper(
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        AlertDialog.Builder(this@ServerAddressBookActivity)
                                .setTitle(getString(R.string.server_book_del_alert_title))
                                .setMessage(getString(R.string.server_book_del_alert_msg))
                                .setPositiveButton(android.R.string.yes) { _: DialogInterface?, _: Int ->
                                    val position = viewHolder.adapterPosition
                                    val server = adapter.getServerAtPosition(position)
//                                    Toast.makeText(ServerAddressBookActivity.this, "Deleting " +
//                                            server.getServerName(), Toast.LENGTH_LONG).show();
                                    // Delete the server
                                    mServerViewModel.delete(server)
                                }
                                .setNegativeButton(android.R.string.no) { _: DialogInterface?, _: Int -> adapter.notifyItemChanged(viewHolder.adapterPosition) }
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show()
                    }
                })
        helper.attachToRecyclerView(mBinding.serverListRecyclerview)


        // Update the cached copy of the words in the adapter.
        mServerViewModel.allServers.observe(this, { servers: List<Server?>? ->
            adapter.setServers(servers)

            addServerFragment?.let {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.remove(it)
                fragmentTransaction.commit()
                mBinding.addServer.show()
            }
        })
    }

    companion object {
        const val EXTRA_REPLY = "net.schueller.peertube.room.REPLY"
    }
}