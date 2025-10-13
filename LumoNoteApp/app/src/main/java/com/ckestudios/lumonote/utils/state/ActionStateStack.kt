package com.ckestudios.lumonote.utils.state

import android.util.Log
import com.ckestudios.lumonote.data.models.Action

class ActionStateStack {

    private val stack = ArrayList<Action>()

    fun getStackContents(): ArrayList<Action> {

        return stack
    }

    fun isStackEmpty(): Boolean {

        return stack.isEmpty()
    }

    fun getTopActionOfStack(): Action? {

        if (!isStackEmpty()) {

            return stack.last()
        }

        return null
    }

    fun popActionFromStack() {

        if (!isStackEmpty()) {

            stack.remove(stack.last())
        }
    }

    fun pushActionToStack(action: Action) {

        stack.add(action)

        Log.d("TextWatcher", "stack: ${stack.onEach { action.toString() }}")
    }

    fun clearStack() {

        if (!isStackEmpty()) {

            stack.clear()
        }
    }
}