package com.raywenderlich.listmaker

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity(),
                    ListSelectionFragment.OnListItemFragmentInteractionListener {
    private var largeScreen  = false
    private var listFragment : ListDetailFragment?= null
    private var fragmentContainer: FrameLayout? = null
    private var listSelectionFragment: ListSelectionFragment = ListSelectionFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        listSelectionFragment =
            supportFragmentManager
                .findFragmentById(R.id.list_selection_fragment) as ListSelectionFragment

        fragmentContainer = findViewById(R.id.fragment_container)

        largeScreen = (fragmentContainer != null)

        // Shows create dialog
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            showCreateListDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Alert dialog
    private fun showCreateListDialog() {
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.create_list)

        val builder = AlertDialog.Builder(this)
        val listTitleEditText = EditText(this)
        listTitleEditText.inputType = InputType.TYPE_CLASS_TEXT

        builder.setTitle(dialogTitle)
        builder.setView(listTitleEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            val list = TaskList(listTitleEditText.text.toString())
            listSelectionFragment.addList(list)

            dialog.dismiss()
            showListDetail(list)
        }

        builder.create().show()
    }

    // New Intent for ListDetailActivity
    private fun showListDetail(list: TaskList) {

        if (!largeScreen) {
            val listDetailIntent = Intent(this, ListDetailActivity::class.java)
            listDetailIntent.putExtra(INTENT_LIST_KEY, list)

            startActivityForResult(listDetailIntent, LIST_DETAIL_REQUEST_CODE)
        } else {
            title = list.name

            listFragment = ListDetailFragment.newInstance(list)
            listFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, it, getString(R.string.list_fragment_tag))
                    .addToBackStack(null)
                    .commit()
            }

            findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
                showCreateTaskDialog()
            }
        }
    }

    override fun onListItemClicked(list: TaskList) {
        showListDetail(list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == LIST_DETAIL_REQUEST_CODE) {
            data?.let {
                listSelectionFragment
                    .saveList(data.getParcelableExtra<TaskList>(INTENT_LIST_KEY) as TaskList)
            }
        }
    }

    private fun showCreateTaskDialog() {
        val taskEditText = EditText(this)
        taskEditText.inputType = InputType.TYPE_CLASS_TEXT

        AlertDialog.Builder(this)
                .setTitle(R.string.task_to_add)
                .setView(taskEditText)
                .setPositiveButton(R.string.add_task) { dialog, _ ->
                    val task = taskEditText.text.toString()
                    listFragment?.addTask(task)
                    dialog.dismiss()
                }
                .create()
                .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        title = resources.getString(R.string.app_name)

        listFragment?.list?.let {
            listSelectionFragment.listDataManager.saveList(it)
        }

        listFragment?.let {
            supportFragmentManager
                    .beginTransaction()
                    .remove(it)
                    .commit()
            listFragment = null
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            showCreateListDialog()
        }
    }

    companion object {
        const val INTENT_LIST_KEY = "list"
        const val LIST_DETAIL_REQUEST_CODE = 123
    }
}