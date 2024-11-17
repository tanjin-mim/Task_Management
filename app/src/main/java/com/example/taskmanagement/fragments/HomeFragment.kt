package com.example.taskmanagement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagement.R
import com.example.taskmanagement.databinding.FragmentHomeBinding
import com.example.taskmanagement.utils.TaskManagementAdapter
import com.example.taskmanagement.utils.TaskManagementData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), AddTaskManagementPopUpFragment.DialogNextBtnClickListener,
    TaskManagementAdapter.TaskManagementAdapterClickInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentHomeBinding
    private var popUpFragment: AddTaskManagementPopUpFragment? = null
    private lateinit var adapter: TaskManagementAdapter
    private lateinit var mList: MutableList<TaskManagementData>
    private lateinit var btnLogOut: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun registerEvents() {
        binding.addBtnHome.setOnClickListener {
            if (popUpFragment != null)
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            popUpFragment = AddTaskManagementPopUpFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(
                childFragmentManager,
                AddTaskManagementPopUpFragment.TAG
            )
        }

        binding.logOutBtn.setOnClickListener {
            logoutUser()
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
            .child("Tasks")
            .child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = TaskManagementAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val taskData = taskSnapshot.getValue(TaskManagementData::class.java)
                    if (taskData != null) {
                        mList.add(taskData)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveTask(
        taskManagement: String,
        endDateTime: String,
        taskManagementEt: TextInputEditText
    ) {
        // Generate a unique ID for the task
        val taskId = databaseRef.push().key ?: return

        // Create the task object
        val taskData = TaskManagementData(
            taskId = taskId,
            task = taskManagement,
            endDateTime =  endDateTime,
            isCompleted = false,
            isMissed = false
        )

        // Save the task to the database
        databaseRef.child(taskId).setValue(taskData).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task saved successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
            taskManagementEt.text = null
            popUpFragment!!.dismiss()
        }
    }



    override fun onUpdateTask(
        taskManagementData: TaskManagementData,
        taskManagementEt: TextInputEditText
    ) {
        val updatedTask = hashMapOf<String, Any>(
            "task" to taskManagementData.task,
            "endDateTime" to taskManagementData.endDateTime,
            "isCompleted" to taskManagementData.isCompleted,
            "isMissed" to taskManagementData.isMissed
        )

        databaseRef.child(taskManagementData.taskId).updateChildren(updatedTask).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
            }
            taskManagementEt.text = null
            popUpFragment?.dismiss()
        }
    }

    override fun onDeleteTaskBtnClicked(taskManagementData: TaskManagementData) {
        databaseRef.child(taskManagementData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Task deleted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(taskManagementData: TaskManagementData) {
        if (popUpFragment != null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()

        popUpFragment = AddTaskManagementPopUpFragment.newInstance(
            taskManagementData.taskId,
            taskManagementData.task,
            taskManagementData.endDateTime,
            taskManagementData.isCompleted,
            taskManagementData.isMissed
        )
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager, AddTaskManagementPopUpFragment.TAG)
    }

    private fun logoutUser() {
        auth.signOut()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navController.navigate(R.id.action_homeFragment_to_signInFragment)
    }
}
