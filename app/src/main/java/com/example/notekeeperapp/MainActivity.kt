package com.example.notekeeperapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notekeeperapp.NoteKeeperAppWidget.Companion.sendRefreshBroadcast
import com.example.notekeeperapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val tag = this::class.simpleName
    private var notePosition = POSITION_NOT_SET
    private var isNewNote = false
    private var isCancelling = false
    private var noteColor: Int = Color.TRANSPARENT

    private val noteGetTogetherHelper = NoteGetTogetherHelper(this, lifecycle)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(toolbar)
        val adapterCourses = ArrayAdapter(
            this, android.R.layout
                .simple_spinner_item,
            DataManager.courses.values.toList()
        )
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourses.adapter = adapterCourses

        notePosition =
            savedInstanceState?.getInt(NOTE_POSITION, POSITION_NOT_SET) ?: intent.getIntExtra(
                NOTE_POSITION,
                POSITION_NOT_SET
            )

        if (notePosition != POSITION_NOT_SET)
            displayNote()
        else {
            isNewNote = true
            newNote()
            notePosition = DataManager.notes.lastIndex
        }
        Log.d(tag, "On Create")
        val commentsAdapter = CommentRecyclerAdapter(this, DataManager.notes[notePosition])
        commentsList?.layoutManager = LinearLayoutManager(this)
        commentsList?.adapter = commentsAdapter

        colorSelector?.addListener { color ->
            noteColor = color
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        notePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET)
    }

    private fun newNote() {
        DataManager.notes.add(NoteInfo())
        notePosition = DataManager.notes.lastIndex
    }

    private fun displayNote() {
        val note = DataManager.notes[notePosition]
        val coursePosition = DataManager.courses.values.indexOf(note.course)
        textNoteTitle.setText(note.title)
        noteText.setText(note.text)
        colorSelector?.selectedColorValue = note.color
        noteColor = note.color
        spinnerCourses.setSelection(coursePosition)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val message = "No More Notes"
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_remainder -> {
                ReminderNotification.notify(
                    this,
               DataManager.notes[notePosition],
                    notePosition
                )
                true
            }
            R.id.action_settings -> {
                isCancelling = true
                finish()
                true
            }
            R.id.action_next -> {
                if (notePosition < DataManager.notes.lastIndex) {
                    moveNext()
                } else {
                    message.showMessage()
                }
                true
            }

            R.id.action_previous -> {
                if (notePosition > FIRST_NOTE) {
                    showPrevious()
                } else {
                    message.showMessage()
                }
                true
            }
            R.id.action_get_together -> {
                noteGetTogetherHelper.sendMessage(DataManager.loadNote(notePosition))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun String.showMessage() {
        Snackbar.make(textNoteTitle, this, Snackbar.LENGTH_LONG).show()
    }

    private fun showPrevious() {
        --notePosition
        displayNote()
        invalidateOptionsMenu()
    }

    private fun moveNext() {
        ++notePosition
        displayNote()
        // this used to make changes to menu that has being displayed and when it gets called,
        // it calls the onPrepareOptionsMenu
        invalidateOptionsMenu()
    }

    // used to make changes to menuItem at runtime, it is called before the menu is initially
    // displayed, before being called by the invalidateOptionsMenu() to effect changes
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (notePosition >= DataManager.notes.lastIndex) {
            val menuItem = menu?.findItem(R.id.action_next)
            if (menuItem != null) {
                menuItem.icon = ContextCompat.getDrawable(applicationContext,R.drawable.ic_block_black_24)
                menuItem.isEnabled = true
            }
        } else if (notePosition <= 0) {
            val menuItem = menu?.findItem(R.id.action_previous)
            menuItem?.let {
                menuItem.icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_block_black_24)
                menuItem.isEnabled = true
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    // The best time to automatically save changes to an activity is when the activity stops being visible
    // this happens at the onPause method in the activity lifecycle
    override fun onPause() {
        super.onPause()
        saveNote()
        Log.d(tag, "On Pause")
    }

    private fun saveNote() {
        /*select note position property to currently displayed notes from our data manager
        Also make noteInfo class properties nullable*/
        val note = DataManager.notes[notePosition]
        note.title = textNoteTitle.text.toString()
        note.text = noteText.text.toString()
        note.course = spinnerCourses.selectedItem as CourseInfo
        note.color = this.noteColor
        sendRefreshBroadcast(this)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NOTE_POSITION, notePosition)
    }
}