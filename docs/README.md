# Kif User Guide

## Introduction
Kif is a chatbot that assists users in managing tasks efficiently. It supports various task types, including deadlines, events, and simple to-dos. Users can interact with Kif using text-based commands to manage their tasks effectively.

## Features

### Listing Tasks
**Command:** `list`

Displays all tasks currently stored.

**Example Usage:**
```
list
```
**Expected Output:**
```
Here are your tasks:
1. [T] [ ] Read book
2. [D] [ ] Submit assignment (by: Feb 25 2024)
3. [E] [ ] Project meeting (from: Feb 26 2024 to: Feb 26 2024)
```

### Marking Tasks as Done
**Command:** `mark <task_number>`

Marks a specified task as completed.

**Example Usage:**
```
mark 1
```
**Expected Output:**
```
Nice! I've marked this task as done:
[T] [X] Read book
```

### Unmarking Tasks
**Command:** `unmark <task_number>`

Marks a specified task as not completed.

**Example Usage:**
```
unmark 1
```
**Expected Output:**
```
OK, I've marked this task as not done yet:
[T] [ ] Read book
```

### Adding a To-Do
**Command:** `todo <description>`

Adds a new to-do task.

**Example Usage:**
```
todo Buy groceries
```
**Expected Output:**
```
Got it. I've added this task:
[T] [ ] Buy groceries
```

### Adding a Deadline
**Command:** `deadline <description> /by <yyyy-MM-dd>`

Adds a new task with a deadline.

**Example Usage:**
```
deadline Submit assignment /by 2024-02-25
```
**Expected Output:**
```
Got it. I've added this task:
[D] [ ] Submit assignment (by: Feb 25 2024)
```

### Adding an Event
**Command:** `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>`

Adds a new event task.

**Example Usage:**
```
event Project meeting /from 2024-02-26 /to 2024-02-26
```
**Expected Output:**
```
Got it. I've added this task:
[E] [ ] Project meeting (from: Feb 26 2024 to: Feb 26 2024)
```

### Deleting a Task
**Command:** `delete <task_number>`

Removes a task from the list.

**Example Usage:**
```
delete 2
```
**Expected Output:**
```
Noted. I've removed this task:
[D] [ ] Submit assignment (by: Feb 25 2024)
```

### Undoing the Last Command
**Command:** `undo`

Reverts the last task-related command.

**Example Usage:**
```
undo
```
**Expected Output:**
```
The last command has been undone.
```

### Exiting the Application
**Command:** `bye`

Closes the chatbot.

**Example Usage:**
```
bye
```
**Expected Output:**
```
Goodbye! Have a great day!
```

## Error Handling
- If the user enters an invalid command, Kif will respond with an appropriate error message.
- For deadlines, Kif ensures the date format follows `yyyy-MM-dd`. If incorrect, it prompts the user to reformat.

## Command Summary

| Command | Description |
|---------|----------------------------------|
| `list` | Displays all tasks |
| `mark <task_number>` | Marks a task as done |
| `unmark <task_number>` | Unmarks a task |
| `todo <description>` | Adds a to-do task |
| `deadline <description> /by <yyyy-MM-dd>` | Adds a deadline task |
| `event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>` | Adds an event task |
| `delete <task_number>` | Deletes a task |
| `undo` | Undoes the last command |
| `bye` | Exits the application 

## Work In Progress
- The `event` command currently treats dates and times as plain text instead of parsing them.
- The `undo` command does not support redoing: it cannot undo a previous undo action.

## Use of AI-Generated/Assisted Work
- ChatGPT has been used for code refactoring and generating JavaDoc for code that I originally wrote.
