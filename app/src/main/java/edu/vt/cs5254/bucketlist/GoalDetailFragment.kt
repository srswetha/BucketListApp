package edu.vt.cs5254.bucketlist

import ProgressDialogFragment
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import edu.vt.cs5254.bucketlist.GoalNoteType
import edu.vt.cs5254.bucketlist.GoalNote
import edu.vt.cs5254.bucketlist.databinding.FragmentGoalDetailBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.vt.cs5254.bucketlist.adapter.GoalNoteListAdapter
import java.io.File
import java.text.DateFormat
import java.util.Date

class GoalDetailFragment : Fragment() {
    private  val arg: GoalDetailFragmentArgs by navArgs()
    private val vm: GoalDetailViewModel by viewModels{
        GoalDetailViewModelFactory(arg.goalId)
    }
    private lateinit var goalNoteListAdapter: GoalNoteListAdapter
    private var _binding: FragmentGoalDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "FragmentGoalDetailBinding is NULL!!!" }

    private val photoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
            tookPicture ->
        Log.w("!!!GDF ", "Took picture: $tookPicture")
        if (tookPicture){
            vm.goal.value?. let{
                binding.goalPhoto.tag = null
                updatePhoto(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_goal_detail, menu)

                val photoIntent = photoLauncher.contract.createIntent(
                    requireContext(),
                    Uri.EMPTY
                )
                menu.findItem(R.id.take_photo_menu).isVisible =
                    canResolveIntent(photoIntent)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.share_goal_menu -> {
                        vm.goal.value?.let { shareGoal(it) }
                        true
                    }
                    R.id.take_photo_menu -> {
                        vm.goal.value?.let {
                            val photoFile = File(
                                requireContext().applicationContext.filesDir,
                                it.photoFileName
                            )
                            val photoUri = FileProvider.getUriForFile(
                                requireContext(),
                                "edu.vt.cs5254.bucketlist.fileprovider",
                                photoFile
                            )
                            photoLauncher.launch(photoUri)
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
        _binding = FragmentGoalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun shareGoal(goal: Goal) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, goal.title)
            putExtra(Intent.EXTRA_TEXT, getGoalSummary(goal))
        }

        val chooserIntent = Intent.createChooser(
            shareIntent,
            getString(R.string.share_goal)
        )
        startActivity(chooserIntent)
    }
    private fun getGoalSummary(goal: Goal): String {
        val dateString = android.text.format.DateFormat.format(
            "yyyy-MM-dd 'at' hh:mm:ss A",
            java.util.Date(goal.lastUpdated)
        )

        val progressNotes = goal.notes
            .filter { it.type == GoalNoteType.PROGRESS }
            .joinToString("\n") { " * ${it.text}" }

        return buildString {
            append("${goal.title}\n")
            append("Last updated $dateString\n")
            if (progressNotes.isNotEmpty()) {
                append("Progress:\n")
                append("$progressNotes\n")
            }
            if (goal.isPaused) {
                append("This goal has been Paused.\n")
            } else if (goal.isCompleted) {
                append("This goal has been Completed.\n")
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.w("!!! GDF !!!", "Got args: ${arg.goalId}")

        goalNoteListAdapter = GoalNoteListAdapter()
        binding.goalNoteRecyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = goalNoteListAdapter
        }

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val position = viewHolder.bindingAdapterPosition
                return if (position != RecyclerView.NO_POSITION) {
                    val note = goalNoteListAdapter.getNotes()[position]
                    if (note.type == GoalNoteType.PROGRESS) {
                        ItemTouchHelper.LEFT
                    } else {
                        0
                    }
                } else {
                    0
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val noteToDelete = goalNoteListAdapter.getNotes()[position]
                    vm.updateGoal { oldGoal ->
                        oldGoal.copy().apply {
                            notes = oldGoal.notes.filter { it != noteToDelete }
                        }
                    }
                }
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.goalNoteRecyclerView)




        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                vm.goal.collect{ goal ->
                    goal?.let {
                        updateView(it)
                    }
                }
            }
        }

        parentFragmentManager.setFragmentResultListener(
            ProgressDialogFragment.REQUEST_KEY, viewLifecycleOwner
        ) { requestKey, bundle ->
            if (requestKey == ProgressDialogFragment.REQUEST_KEY) {
                val progressText = bundle.getString(ProgressDialogFragment.BUNDLE_KEY)
                if (!progressText.isNullOrBlank()) {
                    Log.d("GoalDetailFragment", "Adding note: $progressText")
                    vm.updateGoal { oldGoal ->
                        oldGoal.copy().apply {
                            notes = oldGoal.notes + GoalNote(
                                text = progressText,
                                type = GoalNoteType.PROGRESS,
                                goalId = oldGoal.id

                            )
                        }
                    }
                }
            }
        }

        binding.addProgressButton?.setOnClickListener {
            val dialog = ProgressDialogFragment()
            dialog.show(parentFragmentManager, "ProgressDialogFragment")
        }

        binding.goalPhoto.setOnClickListener{
            vm.goal.value?.let{
                findNavController().navigate(
                    GoalDetailFragmentDirections.showImageDetail(it.photoFileName)

                )
            }

        }
        binding.pausedCheckbox.setOnClickListener {
            vm.updateGoal { oldGoal ->
                oldGoal.copy().apply { notes =
                    if(oldGoal.isPaused){
                        oldGoal.notes.filter { it.type != GoalNoteType.PAUSED }
                    } else{
                        oldGoal.notes +GoalNote(
                            type = GoalNoteType.PAUSED,
                            goalId = oldGoal.id
                        )
                    }
                }
            }


        }

        binding.completedCheckbox.setOnClickListener {
            vm.updateGoal { oldGoal ->
                oldGoal.copy().apply { notes =
                    if(oldGoal.isCompleted){
                        oldGoal.notes.filter { it.type != GoalNoteType.COMPLETED }
                    } else{
                        oldGoal.notes +GoalNote(
                            type = GoalNoteType.COMPLETED,
                            goalId = oldGoal.id
                        )
                    }
                }
            }

        }

        binding.titleText.doOnTextChanged { text,_, _, _ ->
            vm.updateGoal { oldGoal ->
                oldGoal.copy(title = text.toString()).apply { notes = oldGoal.notes }
            }

        }


        binding.titleText.addTextChangedListener { editable ->
            vm.goal.value?.let { goal ->
                val newTitle = editable.toString()
                if (goal.title != newTitle) {
                    goal.title = newTitle
                    updateView(goal)
                }
            }
        }




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateView(goal: Goal) {
        if(binding.titleText.text.toString() != goal.title){
            binding.titleText.setText(goal.title)
        }


        val formattedDate = android.text.format.DateFormat.format(
            "'Last updated' yyyy-MM-dd 'at' hh:mm:ss A",
            Date(goal.lastUpdated)
        )
        binding.lastUpdatedText.text = formattedDate

        binding.pausedCheckbox.isChecked = goal.isPaused
        binding.completedCheckbox.isChecked = goal.isCompleted

        binding.pausedCheckbox.isEnabled = !goal.isCompleted
        binding.completedCheckbox.isEnabled = !goal.isPaused

        binding.addProgressButton.visibility =
            if (goal.isCompleted) View.GONE else View.VISIBLE

        binding.goalNoteRecyclerView?.let {
            goalNoteListAdapter.submitList(goal.notes)
        }

        Log.d("GoalDetailFragment", "Updating RecyclerView with notes: ${goal.notes}")
        goalNoteListAdapter.submitList(goal.notes)

        updatePhoto(goal)

    }

    private fun updatePhoto(goal: Goal){
        if (binding.goalPhoto.tag != goal.photoFileName){
            val photoFile = File(
                requireContext().applicationContext.filesDir,
                goal.photoFileName
            )
            if (photoFile.exists()){
                Log.w("!!! GDF!!!", "Photo exists; scaling to ImageView")
                binding.goalPhoto.doOnLayout { imageView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        imageView.width,
                        imageView.height
                    )
                    binding.goalPhoto.setImageBitmap(scaledBitmap)
                    binding.goalPhoto.tag = goal.photoFileName
                    binding.goalPhoto.isEnabled = true
                }
            }else {
                Log.w("!!! GDF !!!", "Photo NOT present")
                binding.goalPhoto.setImageBitmap(null)
                binding.goalPhoto.tag = null
                binding.goalPhoto.isEnabled = false
                binding.goalPhoto.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.google.android.material.R.color.material_dynamic_neutral50

                    )
                )
            }
        } else{
            Log.w("!!! GDF !!!", "Photo found, No scaling required")
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        return requireActivity().packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null
    }


}
