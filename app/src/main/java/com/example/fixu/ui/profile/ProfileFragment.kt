package com.example.fixu.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.fixu.ui.auth.LoginActivity
import com.example.fixu.database.SessionManager
import com.example.fixu.databinding.FragmentProfileBinding
import com.example.fixu.utils.ReminderViewModelFactory
import com.google.android.material.materialswitch.MaterialSwitch
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.fixu.utils.ReminderReceiver
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    private lateinit var switchReminder: MaterialSwitch
    private lateinit var switchDarkMode: MaterialSwitch
    private lateinit var tvReminderTime: TextView
    private lateinit var reminderViewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = ReminderViewModelFactory(requireActivity().applicationContext)
        reminderViewModel = ViewModelProvider(this, factory)[ReminderViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater,container, false)
        val view = binding.root

        switchReminder = binding.switchReminder
        switchDarkMode = binding.switchDarkMode
        tvReminderTime = binding.tvReminderTime
        setupDarkModeSwitch()

        reminderViewModel.isReminderEnabled.observe(viewLifecycleOwner) { isEnabled ->
            switchReminder.isChecked = isEnabled
        }
        reminderViewModel.reminderTimeString.observe(viewLifecycleOwner) { time ->
            tvReminderTime.text = time
        }
        tvReminderTime.setOnClickListener {
            showTimePicker()
        }
        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (reminderViewModel.reminderTime.value == Pair(-1, -1)) {
                    requestExactAlarmPermission()
                } else {
                    reminderViewModel.reminderTime.observe(viewLifecycleOwner) { time ->
                        val (selectedHour, selectedMinute) = time
                        setDailyReminder(selectedHour, selectedMinute)
                    }
                }
            } else {
                cancelReminder()
                reminderViewModel.saveReminderState(false)
            }
        }


        binding.btnLogout.setOnClickListener {
            logoutAccount()
        }

        val userName = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()

        binding.tvUsername.text = userName
        binding.tvUserEmail.text = email

        return view
    }

    private fun logoutAccount() {
        sessionManager.clearSession()
        Firebase.auth.signOut()
        moveToSignIn()
    }

    private fun setupDarkModeSwitch() {
        val sharedPref = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("DARK_MODE", false)

        // Set initial switch state
        switchDarkMode.isChecked = isDarkMode
        setAppTheme(isDarkMode)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("DARK_MODE", isChecked).apply()
            setAppTheme(isChecked)
        }
    }

    private fun setAppTheme(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun moveToSignIn() {
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            reminderViewModel.saveReminderTimeString(formattedTime)
            reminderViewModel.saveReminderState(true)
            reminderViewModel.saveReminderTime(selectedHour, selectedMinute)
        }, hour, minute, true)

        timePickerDialog.show()
    }


    private fun setDailyReminder(hour: Int, minute: Int) {

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }
        calendar.timeZone = TimeZone.getDefault()
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("MainActivity", "Failed to set exact alarm: ${e.message}")
            Toast.makeText(requireContext(), "Exact alarm permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cancelReminder() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                switchReminder.isChecked = false
                AlertDialog.Builder(requireContext())
                    .setTitle("Exact Alarm Permission")
                    .setMessage("To schedule alarms at exact times, we need your permission to set exact alarms on your device. Please grant this permission in the settings.")
                    .setPositiveButton("Go to Settings") { _, _ ->
                        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                showTimePicker()
                reminderViewModel.reminderTime.observe(viewLifecycleOwner) { time ->
                    val (selectedHour, selectedMinute) = time
                    setDailyReminder(selectedHour, selectedMinute)
                }
            }
        }
    }

}