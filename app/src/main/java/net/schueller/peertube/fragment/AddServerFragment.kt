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
package net.schueller.peertube.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.schueller.peertube.R
import net.schueller.peertube.activity.SearchServerActivity
import net.schueller.peertube.database.Server
import net.schueller.peertube.database.ServerViewModel
import net.schueller.peertube.databinding.FragmentAddServerBinding
import net.schueller.peertube.helper.APIUrlHelper
import net.schueller.peertube.utils.hideKeyboard

class AddServerFragment : Fragment() {


    private lateinit var mBinding: FragmentAddServerBinding

    private val mServerViewModel: ServerViewModel by activityViewModels()

    private var mServer: Server? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mServer = it.getParcelable(SERVER_ARG)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = FragmentAddServerBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initServerEdit()

        mBinding.addServerButton.setOnClickListener {
            var formValid = true

            hideKeyboard()

            if (mBinding.serverLabel.text.toString().isBlank()) {
                mBinding.serverLabel.error = getString(R.string.server_book_label_is_required)
                Toast.makeText(context, R.string.invalid_url, Toast.LENGTH_LONG).show()
                formValid = false
            }

            // validate url
            mBinding.serverUrl.apply {
                APIUrlHelper.cleanServerUrl(text.toString())?.let {

                    setText(it)

                    if (!Patterns.WEB_URL.matcher(it).matches()) {
                        error = getString(R.string.server_book_valid_url_is_required)
                        Toast.makeText(context, R.string.invalid_url, Toast.LENGTH_LONG).show()
                        formValid = false
                    }
                }
            }


            if (formValid) {
                mBinding.apply {
                    val server = Server(serverName = serverLabel.text.toString())

                    server.serverHost = serverUrl.text.toString()
                    server.username = serverUsername.text.toString()
                    server.password = serverPassword.text.toString()

                    mServerViewModel.insert(server)
                }
            }
        }

        mBinding.pickServerUrl.setOnClickListener {
            val intentServer = Intent(activity, SearchServerActivity::class.java)
            this.startActivityForResult(intentServer, PICK_SERVER)
        }
    }

    private fun initServerEdit() {
        mServer?.let {
            mBinding.apply {
                serverLabel.setText(it.serverName)
                serverUrl.setText(it.serverHost)
                serverUsername.setText(it.username)
                serverPassword.setText(it.password)

                addServerButton.text = getString(R.string.server_book_add_save_button)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PICK_SERVER) {
            return
        }

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val serverUrlTest = data?.getStringExtra("serverUrl")
        //Log.d(TAG, "serverUrl " + serverUrlTest);

        mBinding.serverUrl.setText(serverUrlTest)

        mBinding.serverLabel.apply {
            if (text.toString().isBlank()) {
                setText(data?.getStringExtra("serverName"))
            }
        }

    }

    companion object {
        private const val TAG = "AddServerFragment"
        private const val PICK_SERVER = 1

        private const val SERVER_ARG = "server"

        fun newInstance(server: Server) = AddServerFragment().apply {
            arguments = Bundle().also {
                it.putParcelable(SERVER_ARG, server)
            }
        }

    }
}