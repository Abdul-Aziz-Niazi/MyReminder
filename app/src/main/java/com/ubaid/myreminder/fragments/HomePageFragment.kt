package com.ubaid.myreminder.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ubaid.myreminder.R
import com.ubaid.myreminder.ReminderViewModel
import com.ubaid.myreminder.adapter.TodayReminderAdapter
import com.ubaid.myreminder.auth.FireBaseAuth
import com.ubaid.myreminder.auth.LogInPageFragment
import com.ubaid.myreminder.data.ReminderData
import com.ubaid.myreminder.databinding.HomePageFragmentBinding
import kotlin.math.roundToInt

class HomePageFragment : BaseFragment(R.layout.home_page_fragment) {
    private lateinit var binding: HomePageFragmentBinding
    private lateinit var reminderViewModel: ReminderViewModel
    private var todayAdapter = TodayReminderAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomePageFragmentBinding.bind(view)
        reminderViewModel = ViewModelProvider(requireActivity())[ReminderViewModel::class.java]
        binding.recyclerView.adapter = todayAdapter
        setupListeners()

        if (Firebase.auth.currentUser != null){
            binding.userNameText.text = "Hi ${Firebase.auth.currentUser?.displayName}"
            Glide.with(this).load(Firebase.auth.currentUser?.photoUrl).into(binding.loginIcon)
        } else {
            binding.userNameText.text = "Welcome"
        }

    }

    private fun setupListeners() {
        todayAdapter.isCompletedListener = { reminderData, isChecked ->
            reminderData.isDone = isChecked
            reminderViewModel.update(reminderData)
        }
        reminderViewModel.reminderTaskLiveData.observe(viewLifecycleOwner) { dataList ->
            todayAdapter.todayReminderList = dataList
            todayAdapter.notifyDataSetChanged()
            updateUserProgress(dataList)
        }

        binding.newReminderBtn.setOnClickListener {
            addFragment(NewTaskFragment())
        }

        binding.btnSeeAll.setOnClickListener {
            addFragment(AllTasksFragment())
        }
        binding.header.setOnClickListener {
            replaceFragmentAndAddToBackStack(LogInPageFragment())
        }

    }

    private fun updateUserProgress(dataList: ArrayList<ReminderData>) {
        val taskCount = "${dataList.size} Tasks"
        binding.taskCount.text = taskCount
        val completed = dataList.count { it.isDone }
        binding.taskProgress.progress = ((completed.toFloat() / dataList.size) * 100).roundToInt()
        binding.taskProgressText.text = String.format("%2d%%",binding.taskProgress.progress)
    }
}