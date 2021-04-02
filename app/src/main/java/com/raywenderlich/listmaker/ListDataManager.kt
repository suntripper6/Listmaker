package com.raywenderlich.listmaker

import android.content.Context
import androidx.preference.PreferenceManager


class ListDataManager(private val context: Context) {
    // Persist list to sharedPreferences
    fun saveList(list: TaskList) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit()
        sharedPreferences.putStringSet(list.name, list.tasks.toHashSet())
        sharedPreferences.apply()
    }

    // Retrieve lists from shared preferences
    fun readLists(): ArrayList<TaskList> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val sharedPreferenceContents = sharedPreferences.all
        val taskLists = ArrayList<TaskList>()

        for (taskList in sharedPreferenceContents) {
            val itemsHasSet = ArrayList(taskList.value as HashSet<String>)
            val list  = TaskList(taskList.key, itemsHasSet)
            taskLists.add(list)
        }

        return taskLists
    }
}