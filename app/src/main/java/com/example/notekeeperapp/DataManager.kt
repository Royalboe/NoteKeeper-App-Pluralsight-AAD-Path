package com.example.notekeeperapp

object DataManager {
    val courses = mutableMapOf<String, CourseInfo>()
    val notes = mutableListOf<NoteInfo>()

    init {
        initializeCourses()
        initializeNotes()
    }

    /*fun loadNotes(): List<NoteInfo> {
        simulateLoadDelay()
        return notes
    }*/

    fun loadNotes(vararg noteIds: Int): List<NoteInfo> {
        simulateLoadDelay()
        val noteList: List<NoteInfo>

        if(noteIds.isEmpty())
            noteList = notes
        else {
            noteList = ArrayList(noteIds.size)
            for(noteId in noteIds)
                noteList.add(notes[noteId])
        }
        return noteList
    }

    fun loadNote(noteId: Int) = notes[noteId]

    fun isLastNoteId(noteId: Int) = noteId == notes.lastIndex

    fun idOfNote(note: NoteInfo) = notes.indexOf(note)

    fun noteIdsAsIntArray(notes: List<NoteInfo>): IntArray {
        val noteIds = IntArray(notes.size)
        for(index in 0..notes.lastIndex)
            noteIds[index] = idOfNote(notes[index])
        return noteIds
    }


    // test-driven development starts by implementing code in a way that will fail the test
    // In test driven development we write the test first and then implement the code to fulfil requirement of the test
    fun addNote(course: CourseInfo, noteTitle: String, noteText: String): Int {
        val note = NoteInfo(course, noteTitle, noteText)
        notes.add(note)
        return notes.lastIndex
    }

    fun findNote(course: CourseInfo, noteTitle: String, noteText: String): NoteInfo? {
        for(note in notes)
            if(course == note.course &&
                noteTitle == note.title &&
                noteText == note.text)
                return note
        return null
    }

    private fun simulateLoadDelay() {
        Thread.sleep(1000)
    }
    private fun initializeCourses() {
        var course = CourseInfo("android_intents", "Android Programming with Intents")
        courses[course.courseId] = course

        course = CourseInfo("android_async", "Android Async Programming and Services")
        courses[course.courseId] = course

        course = CourseInfo("java_lang", "Java Fundamentals: The Java Language")
        courses[course.courseId] = course

        course = CourseInfo("java_core", "Java Fundamentals: The Core Platform")
        courses[course.courseId] = course
    }

    fun initializeNotes() {
        //create instances of note info class
        var course = courses["android_intents"]!!
        var note = NoteInfo(
            course, "Dynamic intent resolution",
            "Wow, intents allow components to be resolved at runtime"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)
        note = NoteInfo(
            course, "Delegating intents",
            "PendingIntents are powerful; they delegate much more than just a component invocation"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)

        course = courses["android_async"]!!
        note = NoteInfo(
            course, "Service default threads",
            "Did you know that by default an Android Service will tie up the UI thread?"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)
        note = NoteInfo(
            course, "Long running operations",
            "Foreground Services can be tied to a notification icon"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)

        course = courses["java_lang"]!!
        note = NoteInfo(
            course, "Parameters",
            "Leverage variable-length parameter lists"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)
        note = NoteInfo(
            course, "Anonymous classes",
            "Anonymous classes simplify implementing one-use types"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)

        course = courses["java_core"]!!
        note = NoteInfo(
            course, "Compiler options",
            "The -jar option isn't compatible with with the -cp option"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)
        note = NoteInfo(
            course, "Serialization",
            "Remember to include SerialVersionUID to assure version compatibility"
        )
        note.comments.add(NoteComment("Jeff", "Excellent Point!", System.currentTimeMillis()))
        note.comments.add(NoteComment("Dave", "We should review this", System.currentTimeMillis() - 5000))
        note.comments.add(NoteComment("Lucy", "This is good to know", System.currentTimeMillis() - 10000))
        notes.add(note)
    }
}