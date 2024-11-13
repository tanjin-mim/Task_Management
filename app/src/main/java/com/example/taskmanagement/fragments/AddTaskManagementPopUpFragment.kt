package com.example.taskmanagement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.taskmanagement.R
import com.example.taskmanagement.databinding.FragmentAddTaskManagementPopUpBinding
import com.example.taskmanagement.utils.TaskManagementData
import com.google.android.material.textfield.TextInputEditText


class AddTaskManagementPopUpFragment : DialogFragment() {

    private lateinit var binding : FragmentAddTaskManagementPopUpBinding
    private lateinit var listener : DialogNextBtnClickListener
    private var taskManagementData : TaskManagementData? = null

    fun setListener(listener: DialogNextBtnClickListener){
        this.listener = listener
    }




    companion object{
        const val TAG = "AddTaskManagementPopUpFragment"

        @JvmStatic
        fun  newInstance(taskId:String,task:String) = AddTaskManagementPopUpFragment().apply {
            arguments = Bundle().apply {
                putString("taskId",taskId)
                putString("task",task)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddTaskManagementPopUpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments != null){

            taskManagementData = TaskManagementData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString()
            )
            binding.taskManagementEt.setText(taskManagementData?.task)
        }

        registerEvents()
    }
    private fun registerEvents(){
        binding.taskManagementNextBtn.setOnClickListener {
            val taskManagementTask = binding.taskManagementEt.text.toString()
            if (taskManagementTask.isNotEmpty()){
                if (taskManagementData == null){
                    listener.onSaveTask(taskManagementTask,binding.taskManagementEt)

                }else{
                    taskManagementData?.task = taskManagementTask
                    listener.onUpdateTask(taskManagementData!!, binding.taskManagementEt)
                }


            }else{
                Toast.makeText(context,"Please type some task",Toast.LENGTH_SHORT).show()

            }

        }
        binding.TaskManagementClose.setOnClickListener {
            dismiss()
        } 
    }
    interface DialogNextBtnClickListener{
        fun onSaveTask(taskManagement : String,taskManagementEt : TextInputEditText)
        fun onUpdateTask(taskManagementData :TaskManagementData, taskManagementEt : TextInputEditText)

    }


}