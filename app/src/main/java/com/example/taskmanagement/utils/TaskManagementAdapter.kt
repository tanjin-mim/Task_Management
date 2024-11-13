package com.example.taskmanagement.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanagement.databinding.EachTaskmanegementItemBinding

class TaskManagementAdapter(private val list:MutableList<TaskManagementData>):
RecyclerView.Adapter<TaskManagementAdapter.TaskManagementViewHolder>(){

    private var listener:TaskManagementAdapterClickInterface? = null
    fun setListener (listener:TaskManagementAdapterClickInterface){
        this.listener = listener
    }




    inner class TaskManagementViewHolder(val binding:EachTaskmanegementItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskManagementViewHolder {
        val binding = EachTaskmanegementItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TaskManagementViewHolder(binding)
    }
    override fun onBindViewHolder(holder: TaskManagementViewHolder, position: Int)
{
        with(holder){
            with(list[position]){
                binding.taskManagementTask.text = this.task

                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClicked(this)

                }
                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClicked(this)

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
interface TaskManagementAdapterClickInterface{
    fun onDeleteTaskBtnClicked(taskManagementData: TaskManagementData )
    fun onEditTaskBtnClicked(taskManagementData: TaskManagementData)
}

}