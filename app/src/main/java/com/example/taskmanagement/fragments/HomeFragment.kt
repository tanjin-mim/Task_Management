package com.example.taskmanagement.fragments

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanagement.R
import com.example.taskmanagement.databinding.FragmentHomeBinding
import com.example.taskmanagement.utils.TaskManagementAdapter
import com.example.taskmanagement.utils.TaskManagementData
import com.google.android.gms.tasks.Tasks
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment(), AddTaskManagementPopUpFragment.DialogNextBtnClickListener,
    TaskManagementAdapter.TaskManagementAdapterClickInterface {

    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRef : DatabaseReference
    private  lateinit var navController: NavController
    private lateinit var binding : FragmentHomeBinding
    private  var popUpFragment: AddTaskManagementPopUpFragment ?= null
    private lateinit var adapter: TaskManagementAdapter
    private lateinit var mList: MutableList<TaskManagementData>
    private lateinit var btnLogOut : Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()





    }








    private fun registerEvents(){
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
private fun init(view : View){
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

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val taskManagementTask = taskSnapshot.key?.let {
                        TaskManagementData(it, taskSnapshot.value.toString())
                    }
                    if (taskManagementTask != null) {
                        mList.add(taskManagementTask)
                    }
                }
                adapter.notifyDataSetChanged()
                 
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onSaveTask(taskManagement: String, taskManagementEt: TextInputEditText) {
        databaseRef.push().setValue(taskManagement).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(context,"taskManagement saved successfully !!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()


            }

            taskManagementEt.text = null
            popUpFragment!!.dismiss()
        }

       }

    override fun onUpdateTask(
        taskManagementData: TaskManagementData,
        taskManagementEt: TextInputEditText
    ) {
        val map = HashMap<String,Any>()
            map[taskManagementData.taskId] = taskManagementData.task
            databaseRef.updateChildren(map).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                }
                taskManagementEt.text = null
                popUpFragment!!.dismiss()
            }

    }


    override fun onDeleteTaskBtnClicked(taskManagementData: TaskManagementData) {
        databaseRef.child(taskManagementData.taskId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditTaskBtnClicked(taskManagementData: TaskManagementData) {
        if (popUpFragment!=null)
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        popUpFragment = AddTaskManagementPopUpFragment.newInstance(taskManagementData.taskId,taskManagementData.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager,AddTaskManagementPopUpFragment.TAG)

    }

    private fun logoutUser() {
        auth.signOut() // Firebase sign out
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Navigate to Sign In Fragment
        navController.navigate(R.id.action_homeFragment_to_signInFragment)
    }


}

